package com.lzb.fastJson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class FastJsonTest {
    public static void main(String[] args) {
        // 传入Runnable实例并立刻运行:
//        Thread vt = Thread.startVirtualThread(() -> {
//            System.out.println("Start virtual thread...");
//            Thread.sleep(10);
//            System.out.println("End virtual thread.");
//        });
        // JSON 字符串
        String s = "{\"name\":\"lzb\", \"age\": 99}";

        // 使用JSON对象 将JSON字符串反序列化为JavaBean
        var o = JSON.parseObject(s);
        System.out.println(o);

        var age = o.get("age");
        System.out.println(age);

        var p = JSON.parseObject(s, Person.class);
        System.out.println(p);
    }
}

class Person {
    @JSONField(name = "DATE OF BIRTH", deserialize = false)
    private String name;

    @JSONField(ordinal = 3)
    private int age;
}