package com.lzb.xdevelop.nioserver;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>Title: ��Ӧ�߳�</p>
 * <p>Description: ������ͻ��˷�����Ϣ</p>
 * @author starboy
 * @version 1.0
 */

public final class Writer extends Thread {
    private static List pool = new LinkedList();
    private static Notifier notifier = Notifier.getNotifier();

    public Writer() {
    }

    /**
     * SMS�����߳����ط��񷽷�,������������������
     */
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

                // ����д�¼�
                write(key);
            }
            catch (Exception e) {
                continue;
            }
        }
    }

    /**
     * ������ͻ���������
     * @param key SelectionKey
     */
    public void write(SelectionKey key) throws InterruptedException {

        try {
            SocketChannel sc = (SocketChannel) key.channel();
            Response response = new Response(sc);

            // ����onWrite�¼�
            notifier.fireOnWrite((Request)key.attachment(), response);

            ByteBuffer sendBuffer = ByteBuffer.allocate(1024);
            String str = "123\n";
            sendBuffer.clear();
            sendBuffer.put(str.getBytes());
            sendBuffer.flip();
            sc.write(sendBuffer);
            // �ر�
//            sc.finishConnect();
//            sc.socket().close();
//            sc.close();

            // ����onClosed�¼�
//            notifier.fireOnClosed((Request)key.attachment());

            // �ύ�����߳̽��ж�����ע����ź�
            Server.processReadRequest(key);

//            System.out.println("write sleep 5s...");
//            Thread.sleep(6000);
//            System.out.println("write wakeup...");
        }
        catch (Exception e) {
            notifier.fireOnError("Error occured in Writer: " + e.getMessage());
        }
    }

    /**
     * ����ͻ�����,�����û��������,�����Ѷ����е��߳̽��д���
     */
    public static void processRequest(SelectionKey key) {
        synchronized (pool) {
            pool.add(pool.size(), key);
            pool.notifyAll();
        }
    }
}
