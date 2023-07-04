package com.lzb.redis.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class RedisClient {
    private final String host;
    private final int port;
    private final EventLoopGroup eventLoopGroup;
    private final Bootstrap bootstrap;

    public RedisClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.eventLoopGroup = new NioEventLoopGroup();
        this.bootstrap = new Bootstrap()
                .group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new RedisChannelInitializer());
    }

    public Promise<String> get(String key) {
        Promise<String> promise = new DefaultPromise<>(eventLoopGroup.next());
        RedisRequest request = new RedisRequest("GET", key);
        bootstrap.connect(host, port)
                .addListener((ChannelFutureListener) channelFuture -> {
                    if (channelFuture.isSuccess()) {
                        channelFuture.channel().pipeline().addLast(new RedisResponseListener(promise));
                        channelFuture.channel().writeAndFlush(request.getRequestBytes());
                    } else {
                        promise.setFailure(channelFuture.cause());
                    }
                });
        return promise;
    }

    private class RedisChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new RedisDecoder());
            pipeline.addLast(new RedisEncoder());
        }
    }
//
//    private class RedisDecoder extends io.netty.handler.codec.redis.RedisDecoder {
//    }

    private class RedisEncoder extends io.netty.handler.codec.redis.RedisEncoder {
    }

    public static void main(String args[]) throws InterruptedException {
        var redisClient = new RedisClient("localhost", 6379);

        var p = redisClient.get("key");
        p.await();
    }
}
