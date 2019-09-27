package com.lzb.nio;

import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.excel.constant.ExcelXmlConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/*

读取不会阻止.在当前线程中执行此操作.你只是以这种方式解决无穷无尽的问题.在移交给读取线程之前,您必须取消注册OP_READ,这很容易,
但困难的是当读取线程完成读取时,它必须重新注册OP_READ,这需要(i)选择器wakeup(),
导致select线程在可能无事可做时运行,这很浪费,或者(ii)使用挂起重新注册队列,这会延迟该通道上的下一次读取,
直到下一次选择器之后醒来,这也是浪费,否则你必须在添加到队列时立即唤醒选择器,如果没有准备就绪也是浪费.
我从未见过使用不同选择和读取线程的令人信服的NIO架构.
不要这样做.如果必须具有多线程,请将通道组织成组,每个组都有自己的选择器和自己的线程,并让所有这些线程都自己读取.

类似地,不需要在单独的线程中写入.只要你有写东西就写.


 You should just write. If write() returns zero, then you should
 (a) register the channel for OP_WRITE;
 (b) retry the write when the channel becomes writable;
 and (c) deregister the channel for OP_WRITE once the write has completed.

 如果有channel在Selector上注册了SelectionKey.OP_WRITE事件，在调用selector.select();时，
 系统会检查内核写缓冲区是否可写（什么时候是不可写的呢，比如缓冲区已满，channel调用了shutdownOutPut等等），
 如果可写，selector.select();立即返回，随后进入key.isWritable()分支。

 https://segmentfault.com/a/1190000017777939
 */
public class Nio implements Runnable {
    //private Selector selector;
    // 静态成员变量，属于类的
    public static ServerSocketChannel ssc;

    // 静态块，初始化静态成员变量
    static {
        try {
            // 打开服务器套接字通道
            ssc = ServerSocketChannel.open();
            // 服务器配置为非阻塞
            ssc.configureBlocking(false);
            // 进行服务的绑定
            ssc.bind(new InetSocketAddress("localhost", 8001));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ByteBuffer sendBuffer = ByteBuffer.allocate(1024);//调整缓存的大小可以看到打印输出的变化
    private ByteBuffer readBuffer = ByteBuffer.allocate(10);
    private ByteBuffer bigBuffer = ByteBuffer.allocate(1024*1024*10);
    String str;
    ThreadPoolExecutor executor;

    public static void main(String[] args) throws IOException {
        System.out.println("server started...");
        new Nio().start();
    }

    public void start() throws IOException {
//        // 打开服务器套接字通道
//        ssc = ServerSocketChannel.open();
//        // 服务器配置为非阻塞
//        ssc.configureBlocking(false);
//        // 进行服务的绑定
//        ssc.bind(new InetSocketAddress("localhost", 8001));

//        // 通过open()方法找到Selector
//        selector = Selector.open();
//        // 注册到selector，等待连接
//        ssc.register(selector, SelectionKey.OP_ACCEPT);

        executor = new ThreadPoolExecutor(4, 8, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(12));

        // 将通道组织成组,每个组都有自己的选择器和自己的线程,并让所有这些线程都自己读取.
        executor.execute(new Nio());
//        executor.execute(new Nio());
//        executor.execute(new Nio());
//        executor.execute(new Nio());
//        executor.execute(new Nio());
//        executor.execute(new Nio());
        ThreadUtil.sleep(1000*100);
//        while (!Thread.currentThread().isInterrupted()) {
//            selector.select();
//            Set<SelectionKey> keys = selector.selectedKeys();
//            Iterator<SelectionKey> keyIterator = keys.iterator();
//            while (keyIterator.hasNext()) {
//                SelectionKey key = keyIterator.next();
//                keyIterator.remove(); // 该事件已经处理，可以丢弃
//
//                if (!key.isValid()) {
//                    continue;
//                }
//                if (key.isAcceptable()) {
//                    accept(key);
//                } else if (key.isReadable()) {
////                    SocketChannel socketChannel = (SocketChannel) key.channel();
////                    socketChannel.register(selector, 0);
////                    executor.execute(new ReadTask(key, selector));
//                    read(key);
//                } else if (key.isWritable()) {
////                    SocketChannel socketChannel = (SocketChannel) key.channel();
////                    socketChannel.register(selector, 0);
////                    executor.execute(new WriteTask(key, selector));
//                    write(key);
//                }
//                // keyIterator.remove(); // 该事件已经处理，可以丢弃
//            }
//        }
    }

    @Override
    public void run() {
        Selector selector;
        // 通过open()方法找到Selector
        try {
            // 可以打开一组chan
            selector = Selector.open();
            Selector s = Selector.open();
            // 注册到selector，等待连接
            Nio.ssc.register(selector, SelectionKey.OP_ACCEPT);

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    selector.select();
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> keyIterator = keys.iterator();
                    while (keyIterator.hasNext()) {
                        SelectionKey key = keyIterator.next();
                        // 接受、读、写 都需要在一个线程里面
                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isAcceptable()) {
                            accept(key, selector);
                        } else if (key.isReadable()) {
                            read(key, selector);
                        } else if (key.isWritable()) {
                            write(key, selector);
                        }
                        keyIterator.remove(); // 该事件已经处理，可以丢弃
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    private void accept(SelectionKey key, Selector selector) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = ssc.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("a new client connected "+clientChannel.getRemoteAddress() + ", 线程： " + Thread.currentThread());
    }

    private void read(SelectionKey key, Selector selector) throws IOException {
        SocketChannel socketChannel = (SocketChannel) key.channel();

        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();
//        readBuffer.flip();
        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            socketChannel.close();

            return;
        }

        str = new String(readBuffer.array(), 0, numRead);
        System.out.println("read: " + str + " thread:" + Thread.currentThread().getId());
        // socketChannel.register(selector, SelectionKey.OP_WRITE);


        str = "reader write...\n";
        // System.out.println("write:"+str);

        sendBuffer.clear();
        sendBuffer.put(str.getBytes());
        sendBuffer.flip();
        //socketChannel.write(sendBuffer);

        // 新增写事件
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
        str = "";
        for (int i = 0; i < 1000 * 100; i++) {
            str = str + i;
        }
        str = str + "\n";

        System.out.println(str.length());
        bigBuffer.clear();
        bigBuffer.put(str.getBytes());
        bigBuffer.flip();
//        ByteBuffer buffer = new ByteBuffer(1024); //200M的Buffer
//        //注册写事件
//        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
//        key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        //绑定Buffer
        // key.attach(bigBuffer);

        ByteBuffer buffer = (ByteBuffer) key.attachment();
        if (buffer != null) {
            bigBuffer = bigBuffer.put(buffer);
        }

        // 感觉正确的做法是：先什么都不管，只管发送数据，直到发送缓存区满了，再装载事件
        try {
            socketChannel.write(bigBuffer); // 发送是异步的，感觉不需要注册事件
        } catch (IOException e) {
            //注册写事件
            key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
            key.interestOps(key.interestOps() & ~SelectionKey.OP_READ);
        }
    }

    private void write(SelectionKey key, Selector selector) throws IOException, ClosedChannelException {
        SocketChannel channel = (SocketChannel) key.channel();

        // System.out.println("write:"+str);
        //isWritable分支
        if(key.isWritable()) {
            ByteBuffer buffer = (ByteBuffer) key.attachment();
            // SocketChannel channel = (SocketChannel) key.channel();
            if (buffer.hasRemaining()) {
                channel.write(buffer);
            } else {
                //发送完了就取消写事件，否则下次还会进入该分支
                key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                key.interestOps(key.interestOps() | SelectionKey.OP_READ);
            }
        }

        ByteBuffer buffer = (ByteBuffer) key.attachment();
//        sendBuffer.clear();
//        sendBuffer.put(str.getBytes());
//        sendBuffer.flip();
//        channel.write(sendBuffer);

        // channel.register(selector, SelectionKey.OP_READ);

        // key.interestOps(SelectionKey.OP_READ);

        // 取消写事件
        //key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
    }
}

class ReadTask implements Runnable {
    private Selector selector;
    private SelectionKey key;
    private ByteBuffer readBuffer;
    String str;

    public ReadTask(SelectionKey key, Selector selector) {
        this.selector = selector;
        this.key = key;
        readBuffer = ByteBuffer.allocate(1024);//调整缓存的大小可以看到打印输出的变化
    }

    @Override
    public void run() {
        System.out.println("正在执行task ");
        SocketChannel socketChannel = (SocketChannel) key.channel();
        // Clear out our read buffer so it's ready for new data
        this.readBuffer.clear();
        // readBuffer.flip();
        // Attempt to read off the channel
        int numRead;
        try {
            numRead = socketChannel.read(this.readBuffer);
        } catch (IOException e) {
            // The remote forcibly closed the connection, cancel
            // the selection key and close the channel.
            key.cancel();
            try {
                socketChannel.close();
            } catch (IOException e2) {

            }
            return;
        }

        str = new String(readBuffer.array(), 0, numRead);
        System.out.println(str);
        try {
            socketChannel.register(selector, SelectionKey.OP_WRITE);
        } catch (ClosedChannelException e) {

        }
    }
}


class WriteTask implements Runnable {
    private Selector selector;
    private SelectionKey key;
    private ByteBuffer sendBuffer;
    String str;

    public WriteTask(SelectionKey key, Selector selector) {
        this.selector = selector;
        this.key = key;
        sendBuffer = ByteBuffer.allocate(1024);//调整缓存的大小可以看到打印输出的变化
    }

    @Override
    public void run() {
        SocketChannel channel = (SocketChannel) key.channel();
        str = "123\n";
        System.out.println("write:"+str);

        sendBuffer.clear();
        sendBuffer.put(str.getBytes());
        sendBuffer.flip();
        try {
            channel.write(sendBuffer);
        } catch (IOException e) {

        }

        try {
            channel.register(selector, SelectionKey.OP_READ);
        } catch (ClosedChannelException e) {

        }
    }
}