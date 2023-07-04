package com.lzb.redis.lua;

import io.lettuce.core.XReadArgs;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.StreamEntryID;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.jedis.params.XAddParams;

import java.util.HashMap;

public class Main {
    static final String setDefaultQuery = "local etag = redis.pcall(\"HGET\", KEYS[1], \"version\");\n" + "\tif type(etag) == \"table\" then\n" + "\t\tredis.call(\"DEL\", KEYS[1]);\n" + "\tend;\n" + "\tlocal fwr = redis.pcall(\"HGET\", KEYS[1], \"first-write\");\n" + "\tif not etag or type(etag) == \"table\" or etag == \"\" or etag == ARGV[1] or (not fwr and ARGV[1] == \"0\") then\n" + "\t\tredis.call(\"HSET\", KEYS[1], \"data\", ARGV[2]);\n" + "\t\tif ARGV[3] == \"0\" then\n" + "\t\t\tredis.call(\"HSET\", KEYS[1], \"first-write\", 0);\n" + "\t\tend;\n" + "\t\treturn redis.call(\"HINCRBY\", KEYS[1], \"version\", 1)\n" + "\telse\n" + "\t\treturn error(\"failed to set key \" .. KEYS[1])\n" + "\tend";

    public static void main(String[] args) throws InterruptedException {
        String s = "123";
        Object obj = s;
        obj = 1;

        String[] a = new String[2];
        Object[] b = a;
        a[0] = "hi";

        if (args.length == 0) {
            b = new Integer[3];
        }

        b[1] = Integer.valueOf(42);

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);

        try (Jedis jedis = jedisPool.getResource()) {
            // testStream(jedis);

            String setQuery = setDefaultQuery;
            String key = "testKey";
            int ver = 0;
            String bt = "testVal";
            int firstWrite = 1;
            try {
                Object ret = jedis.eval(setQuery, 1, key, String.valueOf(ver), bt, String.valueOf(firstWrite));
                System.out.println(ret.toString() + "\n");

            } catch (JedisDataException e) {
                throw new RuntimeException(e.getMessage(), e);
            }

            // get version
            String result = jedis.hget(key, "version");
            if (result != null) {
                System.out.println(result + "\n");
            }

            // ttl
            int ttl = 10;
            if (ttl > 0) {
                Long ret = jedis.expire(key, ttl);
                if (ret == null || ret != 1) {
                    throw new RuntimeException("failed to set expire for key " + key);
                }
            } else if (ttl < 0) {
                Long ret = jedis.persist(key);
                if (ret == null || ret != 1) {
                    throw new RuntimeException("failed to persist key " + key);
                }
            }

            //
            String consistency = "strong";
            int replicas = 1;
            if (consistency.equals("strong") && replicas > 0) {
                try {
                    jedis.wait(replicas, 1000);
                } catch (InterruptedException e) {
                    throw e;
                }
            }
        }

        // jedisPool.close();
    }

//    public static void testStream(Jedis jedis) {
//        String stream = "testStream";
//
//        Thread producerThread = new Thread(() -> {
//            int val = 0;
//            while (true) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                int finalVal = val++;
//                StreamEntryID id = jedis.xadd(stream,
//                        new XAddParams().maxLen(10).approximateTrimming(),
//                        new HashMap<>() {{
//                            this.put("data", Integer.toString(finalVal));
//                        }});
//                System.out.println("Producer produced message with ID " + id);
//            }
//        });

//        Thread consumerThread = new Thread(() -> {
//            StreamEntryID lastId = new StreamEntryID();
//            while (true) {
//                XReadArgs args = new XReadArgs().block(0).count(1).stream(stream, lastId);
//                for (StreamEntry entry : jedis.xread(args)) {
//                    System.out.println("Consumer received message: " + entry);
//                    lastId = entry.getID();
//                }
//            }
//        });

    // producerThread.start();
    //consumerThread.start();
}



