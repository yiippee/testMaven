package com.lzb.redis;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;

/**
 * 2018/11/19.
 */
public class RedisClientInitializer extends ChannelInitializer<Channel> {

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RedisDecoder());
        pipeline.addLast(new RedisBulkStringAggregator());
        pipeline.addLast(new RedisArrayAggregator());
        pipeline.addLast(new RedisEncoder());
        pipeline.addLast(new RedisClientHandler());
    }
}
