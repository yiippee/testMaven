package com.lzb.genericity;

public class MainTest {
    public static void main(String[] args) {
        String status = "1";
        if ((status != "1") && (status != "2")) {
            throw new NullPointerException("122");
        }
        Box b = new Box();
        b.set(new Apple());
        Apple apple = (Apple) b.get();

        Box<Apple> b2 = new Box<Apple>();
        b2.set(new Apple());
        Apple appole2 = b2.get();
    }
}
