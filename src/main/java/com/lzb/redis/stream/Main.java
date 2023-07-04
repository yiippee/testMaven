package com.lzb.redis.stream;

import redis.clients.jedis.*;
import redis.clients.jedis.params.XAddParams;
import redis.clients.jedis.params.XReadParams;
import redis.clients.jedis.StreamEntryID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    private LinkedBlockingQueue<Map.Entry<String, List<StreamEntry>>> queue = new LinkedBlockingQueue<>(100);
    private JedisPoolConfig poolConfig = new JedisPoolConfig();
    private JedisPool jedisPool = new JedisPool(poolConfig, "localhost", 6379);

    public static void main(String[] args) throws InterruptedException {
        Main test = new Main();
        test.testStream();
    }

    public void testStream() {
        String stream = "testStream";

        Thread producerThread = new Thread(() -> {
            Jedis jedis = jedisPool.getResource();
            int val = 0;
            while (true) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int finalVal = val++;
                StreamEntryID id = jedis.xadd(stream, StreamEntryID.NEW_ENTRY,
                        new HashMap<>() {{
                            this.put("data", Integer.toString(finalVal));
                        }}, 10, true);
                System.out.println("Producer produced message with ID " + id);
            }
        });

        Thread consumerThread = new Thread(() -> {
            Jedis redis = jedisPool.getResource();

            while (true) {
                XReadParams args = new XReadParams().count(1).block(0);
                Map<String, StreamEntryID> e = new HashMap<>();
                StreamEntryID streamEntryID = StreamEntryID.LAST_ENTRY;
                e.put(stream, streamEntryID);
                List<Map.Entry<String, List<StreamEntry>>> list = redis.xread(args, e);
                if (list == null) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    continue;
                }
                for (Map.Entry<String, List<StreamEntry>> entry : list) {
                    try {
                        queue.put(entry);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    System.out.println("Consumer received message: " + entry);
                }
            }
        });

        producerThread.start();
        consumerThread.start();

        // 模拟从数据库加载耗时
        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 消费queue
        while (true) {
            System.out.println("正在从队列中获取数据......");
            try {
                Map.Entry<String, List<StreamEntry>> data = queue.take();
                System.out.println("拿到队列中的数据：" + data);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


