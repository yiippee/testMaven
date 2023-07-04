package com.lzb.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class LettuceTest {
    public static void main(String args[]) throws InterruptedException, ExecutionException {
        RedisClient redisClient = RedisClient.create("redis://192.168.168.176");
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();

//        syncCommands.set("key", "Hello, Redis!");
//        syncCommands.set("key2", "Hello, Redis 2!");
//        String value = syncCommands.get("foo");


        RedisAsyncCommands<String, String> commands = redisClient.connect().async();
        RedisFuture<String> future = commands.get("key");
//        future = commands.get("key2");
//        if (!future.await(1, TimeUnit.MINUTES)) {
//            System.out.println("Could not complete within the timeout");
//        }
//        future.thenAccept(result -> {
//            System.out.println(result);
//        });

        var result = future.get();
        System.out.println(result);

        connection.close();
        redisClient.shutdown();
    }
}
