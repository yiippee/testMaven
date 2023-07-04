package com.lzb.redis.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Promise;

public class RedisResponseListener extends SimpleChannelInboundHandler<String> {
    private final Promise<String> promise;

    public RedisResponseListener(Promise<String> promise) {
        this.promise = promise;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        ctx.pipeline().remove(this);
        promise.setSuccess(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.pipeline().remove(this);
        promise.setFailure(cause);
    }
}