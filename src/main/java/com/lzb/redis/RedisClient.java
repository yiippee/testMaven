package com.lzb.redis;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CountDownLatch;

/**
 * redis 客户端
 * 2018/11/19.
 */
public class RedisClient {

    String host;    //   目标主机
    int port;       //   目标主机端口

    public RedisClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new RedisClientInitializer());

            Channel channel = bootstrap.connect(host, port).sync().channel();
            System.out.println(" connected to host : " + host + ", port : " + port);
            System.out.println(" type redis's command to communicate with redis-server or type 'quit' to shutdown ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//            ChannelFuture lastWriteFuture = null;
            CountDownLatch latch = new CountDownLatch(1);
            for (; ; ) {
                String s = in.readLine();
                if (s.equalsIgnoreCase("quit")) {
                    break;
                }
                System.out.print(">");

                Thread thread = new Thread(() -> {
                    System.out.println("正在运行的线程ID: " + Thread.currentThread().getId());
                    // channel会把需要write的对象放入Channel对应的EventLoop的队列中就返回了，
                    // EventLoop是一个SingleThreadEventExector，它会回调Bootstrap时配置的CommandHandler的write方法
                    var lastWriteFuture = channel.writeAndFlush(s);
                    lastWriteFuture.addListener(new GenericFutureListener<ChannelFuture>() {
                        @Override
                        public void operationComplete(ChannelFuture future) throws Exception {
                            if (!future.isSuccess()) {
                                System.err.print("write failed: ");
                                future.cause().printStackTrace(System.err);
                            } else {
                                // System.out.println("write success.");
                            }
                        }
                    });
                    latch.countDown();
                });
                thread.start();

                latch.await(); // 必须等待线程执行完毕后，才继续执行

//                // channel会把需要write的对象放入Channel对应的EventLoop的队列中就返回了，
//                // EventLoop是一个SingleThreadEventExector，它会回调Bootstrap时配置的CommandHandler的write方法
//                lastWriteFuture = channel.writeAndFlush(s);
//                lastWriteFuture.addListener(new GenericFutureListener<ChannelFuture>() {
//                    @Override
//                    public void operationComplete(ChannelFuture future) throws Exception {
//                        if (!future.isSuccess()) {
//                            System.err.print("write failed: ");
//                            future.cause().printStackTrace(System.err);
//                        } else {
//                            System.out.println("write success.");
//                        }
//                    }
//                });
            }
//            if (lastWriteFuture != null) {
//                lastWriteFuture.sync();
//            }

            System.out.println(" bye ");
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        RedisClient client = new RedisClient("localhost", 6379);
        client.start();
    }

}