package com.lzb.xdevelop.nioserver;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Title: 读线程</p>
 * <p>Description: 该线程用于读取客户端数据</p>
 * @author starboy
 * @version 1.0
 */

public class Reader extends Thread {
    private static List pool = new LinkedList();
    private static Notifier notifier = Notifier.getNotifier();

    public Reader() {
    }

    public void run() {
        while (true) {
            try {
                SelectionKey key;
                synchronized (pool) {
                    while (pool.isEmpty()) {
                        pool.wait();
                    }
                    key = (SelectionKey) pool.remove(0);
                }

                // 读取数据
                read(key);
            }
            catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * 读取客户端发出请求数据
     * @param sc 套接通道
     */
    private static int BUFFER_SIZE = 1024;
    public static byte[] readRequest(SocketChannel sc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int off = 0;
        int r = 0;
        byte[] data = new byte[BUFFER_SIZE * 10];

        while ( true ) {
            buffer.clear();
            r = sc.read(buffer);
            if (r == -1) break;
            if (r == 0) break;
            if ( (off + r) > data.length) {
                data = grow(data, BUFFER_SIZE * 10);
            }
            byte[] buf = buffer.array();
            System.arraycopy(buf, 0, data, off, r);
            off += r;
        }
        byte[] req = new byte[off];
        System.arraycopy(data, 0, req, 0, off);
        return req;
    }

    /**
     * 处理连接数据读取
     * @param key SelectionKey
     */
    public void read(SelectionKey key) {
        System.out.println("read...");
        try {
            // 读取客户端数据
            SocketChannel sc = (SocketChannel) key.channel();
            byte[] clientData =  readRequest(sc);

            Request request = (Request)key.attachment();
            request.setDataInput(clientData);

            // 触发onRead
            notifier.fireOnRead(request);

            // 提交主控线程进行写处理，注册写信号，说明我有数据需要写了
            Server.processWriteRequest(key);

//            System.out.println("reader sleep 5s...");
//            Thread.sleep(3000);
//            System.out.println("reader wakeup...");

            // 做了一些很耗时的操作以后，还可以继续往这个key进行操作了？
            ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
            String str = "88888888\n";
            sendBuffer.clear();
            sendBuffer.put(str.getBytes());
            sendBuffer.flip();
            sc.write(sendBuffer);

        }
        catch (Exception e) {
            notifier.fireOnError("Error occured in Reader: " + e.getMessage());
        }
    }

    /**
     * 处理客户请求,管理用户的联结池,并唤醒队列中的线程进行处理
     */
    public static void processRequest(SelectionKey key) {
        synchronized (pool) {
            pool.add(pool.size(), key);
            pool.notifyAll();
        }
    }

    /**
     * 数组扩容
     * @param src byte[] 源数组数据
     * @param size int 扩容的增加量
     * @return byte[] 扩容后的数组
     */
    public static byte[] grow(byte[] src, int size) {
        byte[] tmp = new byte[src.length + size];
        System.arraycopy(src, 0, tmp, 0, src.length);
        return tmp;
    }
}
