package com.lzb.zookeeper;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.commons.lang3.StringUtils;
import org.apache.zookeeper.Watcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class LockAddrChangedCallback {
    public void onPathLose() {}
    public void onPathLocked() {}
}
/**
 * 使用zookeeper实现分布式锁，可用于执行分布式定时任务等互斥类代码，调用{@link #tryLock()}，并在任务结束后，一定要调用{@link #unlock()}释放锁
 *
 * @author gaohang
 */
public final class ZookeeperLock {
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperLock.class);

    private static final ScheduledExecutorService delayExecutor = Executors.newScheduledThreadPool(1);
    private static final ExecutorService callbackExecutor = Executors.newFixedThreadPool(1);

    /**
     * 所有锁都在此节点下
     */
    private static final String LOCK_BASE_PATH = "/onlineetl/distributeLock/instance";
    private static final Charset UTF8 = Charset.forName("UTF-8");

    private final int delayTime = 10;

    private final String lockName;
    private final String lockPath;
    private final ZkClient zkClient;

    /**
     * 当前机器的ip
     */
    private final String localhostAddr;

    /**
     * 占用锁的机器的ip
     */
    private String activeHostAddr;

    /**
     * 当前持有锁的状态
     */
    private volatile State state = State.CREATED;

    /**
     * 本地锁，防止本地多线程导致本地非串行
     */
    private final Lock localLock = new ReentrantLock();

    /**
     * 创建分布多锁
     *
     * @param lockName 锁的名字，唯一
     * @param zkClient zk客户端
     * @param callback 当有dataDeleted时，重新获取争用后调用
     * @throws UnknownHostException 获取localhost失败
     */
    ZookeeperLock(String lockName, ZkClient zkClient, LockAddrChangedCallback callback) throws UnknownHostException {
        this.zkClient = zkClient;
        if (StringUtils.isBlank(lockName)) {
            throw new IllegalArgumentException("lockName cannot be blank string");
        }
        this.lockName = lockName;
        this.lockPath = LOCK_BASE_PATH + '/' + lockName;
        final InetAddress localhost = InetAddress.getLocalHost();
        this.localhostAddr = localhost.getHostAddress();
        initLock();
        subscriptDataChange(lockName, zkClient, callback);
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState state) throws Exception {
                logger.info("zookeeper state changed, state: {}", state);
            }

            @Override
            public void handleNewSession() throws Exception {
                if (!initLock()) {
                    callbackExecutor.execute(callback::onPathLose);
                }
            }

            @Override
            public void handleSessionEstablishmentError(Throwable error) throws Exception {
                logger.error("failed to connect to zookeeper", error);
            }
        });
    }

    private void subscriptDataChange(String lockName, ZkClient zkClient, LockAddrChangedCallback callback) {
        zkClient.subscribeDataChanges(lockPath, new IZkDataListener() {

            @Override
            public void handleDataChange(String dataPath, Object data) {
                final String newOwner = new String((byte[]) data, UTF8);
                if (!isMyself(newOwner)) {
                    //不是本机
                    state = State.RELEASED;
                    logger.info("lock [{}] released, owner is {}", lockName, newOwner);
                } else {
                    //本机持有了锁
                    state = State.OWN;
                    logger.info("lock [{}] locked", lockName);
                }
                activeHostAddr = newOwner;
            }

            @Override
            public void handleDataDeleted(String dataPath) {
                //数据被删除有两种情况，
                // 1.持有锁的机器挂了
                // 2.网络异常或者zk出现了异常
                //因此在收到数据删除回调时，未获取锁的机器要优先让上一次获取锁的机器重新获取锁
                if (state == State.OWN && activeHostAddr != null && isMyself(activeHostAddr)) {
                    //设置成released，在获取锁成功时防止有新的上锁操作
                    state = State.WAITING;
                    if (localLock.tryLock()) {
                        //说明锁没有在使用中，释放即可，将锁的权限交给其它机器
                        localLock.unlock();
                        activeHostAddr = null;
                        //5s后再获取锁
                        delayExecutor.schedule(() -> {
                            initLock();
                            //对方挂了，自己争用成功
                            callbackExecutor.execute(callback::onPathLocked);
                        }, 5, TimeUnit.SECONDS);
                    } else {
                        //锁被占用，立即获取锁
                        initLock();
                    }
                } else {
                    state = State.WAITING;
                    activeHostAddr = null;
                    //对方可能网络异常了，5s后再获取锁，如果对方5s没恢复，则认为对方短时间内无法恢复了
                    delayExecutor.schedule(() -> {
                        initLock();
                        callbackExecutor.execute(callback::onPathLocked);
                    }, delayTime, TimeUnit.SECONDS);
                }
            }
        });
    }

    private boolean isMyself(String activeHostAddr) {
        return StringUtils.equals(activeHostAddr, localhostAddr);
    }

    private boolean initLock() {
        try {
            zkClient.createEphemeral(lockPath, localhostAddr.getBytes(UTF8));
            activeHostAddr = localhostAddr;
            state = State.OWN;
            logger.info("lock [{}] locked", lockName);
            return true;
        } catch (ZkNodeExistsException e) {
            byte[] host = zkClient.readData(lockPath, true);
            if (host == null) {
                // 如果不存在节点，立即尝试一次
                return initLock();
            } else {
                activeHostAddr = new String(host, UTF8);
                state = State.RELEASED;
                logger.info("lock [{}] released, owner is {}", lockName, activeHostAddr);
                if (isMyself(activeHostAddr)) {
                    state = State.OWN;
                    return true;
                }
                return false;
            }
        } catch (ZkNoNodeException e) {
            zkClient.createPersistent(LOCK_BASE_PATH, true); // 尝试创建父节点
            return initLock();
        } catch (Throwable e) {
            logger.error("lock [{}] failed", lockName, e);
            //不让招出异常
            return false;
        }
    }

    public boolean tryLock() {
        //避免状态被修改，这里先上锁，在handleDataDeleted中，如果trylock成功，则表示没有任务在执行
        if (!localLock.tryLock()) {
            return false;
        }
        switch (state) {
            case OWN:
                if (initLock()) {
                    return true;
                }
                state = State.RELEASED;
                return false;
            case RELEASED:
                localLock.unlock();
                return false;
            case CREATED:
                initLock();
                return relock();
            case WAITING:
                try {
                    TimeUnit.SECONDS.sleep(delayTime);
                } catch (InterruptedException e) {
                    localLock.unlock();
                    throw new RuntimeException(e);
                }
                //再次尝试获取锁
                final boolean locked = relock();
                if (locked) {
                    return true;
                }
                initLock();
                return relock();
        }

        //理论上不会走到这里
        initLock();
        //这里一定要Unlock，否则tryLock会重入，而unlock没有多次释放徜
        return relock();
    }

    private boolean relock() {
        localLock.unlock();
        return tryLock();
    }

    public void unlock() {
        localLock.unlock();
    }

    public String getLockName() {
        return lockName;
    }

    public String getActiveHostAddr() {
        return activeHostAddr;
    }

    @Override
    public String toString() {
        return "ZookeeperLock{" +
                "lockName='" + lockName + '\'' +
                ", lockPath='" + lockPath + '\'' +
                ", localhostAddr='" + localhostAddr + '\'' +
                ", activeHostAddr='" + activeHostAddr + '\'' +
                ", state=" + state +
                '}';
    }

    /**
     * 锁的状态，当状态是created时，则竞争，OWN状态表示持有锁，RELEASED状态表示未持有锁, waiting表示锁待争用
     */
    private enum State {
        OWN, RELEASED, CREATED, WAITING
    }
}