package com.lzb.thread;


import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class CallableThreadTest implements Callable<Integer> {
    public static void main(String[] args) throws InterruptedException {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            // 执行任务
            try {
                System.out.println(Thread.currentThread().threadId() + " doing");
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "任务执行结果";
        }).thenApply(result -> {
            // 处理上一个执行结果
            System.out.println(Thread.currentThread().threadId() + " finish " + result);
            return "新的执行结果";
        });

        System.out.println(Thread.currentThread().threadId() + " waiting");

        Thread.currentThread().sleep(5000);
//        CallableThreadTest ctt = new CallableThreadTest();
//        FutureTask<Integer> ft = new FutureTask<>(ctt);
//        for (int i = 0; i < 100; i++) {
//            System.out.println(Thread.currentThread().getName() + " 的循环变量i的值" + i);
//            if (i == 20) {
//                new Thread(ft, "有返回值的线程").start();
//            }
//        }
//        try {
//            System.out.println("sub thread return：" + ft.get());
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }

    }

    @Override
    public Integer call() throws Exception {
        int i = 0;
        for (; i < 100; i++) {
            System.out.println(Thread.currentThread().getName() + " " + i);
        }
        return i;
    }
}