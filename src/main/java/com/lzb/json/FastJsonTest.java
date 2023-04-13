package com.lzb.json;

import com.alibaba.fastjson2.annotation.JSONField;

public class FastJsonTest {
    @JSONField(name = "AGE")
    private int age;

    @JSONField(name = "FULL NAME")
    private String fullName;

    public static void main(String[] args){
    }
}
