package com.lzb.redis.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

public class RedisRequest {
    private final String command;
    private final String key;

    public RedisRequest(String command, String key) {
        this.command = command;
        this.key = key;
    }

    public ByteBuf getRequestBytes() {
        String request = "*" + 2 + "\r\n" +
                "$" + command.length() + "\r\n" +
                command + "\r\n" +
                "$" + key.length() + "\r\n" +
                key + "\r\n";
        return Unpooled.copiedBuffer(request, CharsetUtil.UTF_8);
    }
}