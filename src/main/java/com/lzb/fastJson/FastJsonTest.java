package com.lzb.fastJson;

import com.alibaba.fastjson.JSON;

public class FastJsonTest {
    public static void main(String[] args) {
        // JSON 字符串
        String s = "{\"formId\":\"{$formId}\",\"link\":\"www.java3y.com\",\"text\":[{\"name\":\"java3y\",\"label\":\"3y\",\"value\":{\"value\":\"{$tureName}\",\"color\":\"\",\"emphasis\":\"\"}},{\"name\":\"java4y\",\"label\":\"3y\",\"value\":{\"value\":\"{$title}\",\"color\":\"\",\"emphasis\":\"\"}},{\"name\":\"java5y\",\"label\":\"5y\",\"value\":{\"value\":\"关注我的公众号，更多干货\",\"color\":\"#ff0040\",\"emphasis\":\"\"}}],\"yyyImg\":\"\",\"yyyAge\":\"\",\"pagepath\":\"\"}";

        // 使用JSON对象 将JSON字符串反序列化为JavaBean
        var o = JSON.parseObject(s);
        System.out.println(o);

        var link = o.getString("link");
        System.out.println(link);
    }
}

