package com.lzb.event;

import org.springframework.beans.factory.annotation.Autowired;

public class MainTest {
    @Autowired
    private MyTestEventPubLisher publisher;

    public static void main(String[] args) {
        MainTest test = new MainTest();
        test.publisher.pushListener("我来了！");
    }
}
