package com.lzb.stream;

import cn.hutool.json.JSON;

import java.util.*;
import java.util.stream.Collectors;

public class MainTest {
    public static void main(String[] args) {
        List<String> keys = Arrays.asList("颜色", "size", "其他");
        List<String> vals = Arrays.asList("白色", "S", "xxx");
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String val = vals.get(i);
            map.put(key, val);
        }

        System.out.println(map); //输出{"a":"aaa","b":"bbb","c":"ccc"}

        List<String> myList = Arrays.asList("a1",  "a2", "b1", "c2", "c1");
        myList.stream().filter(s -> s.startsWith("c")).map(String::toUpperCase).sorted().forEach(System.out::println);
        List<OperationLogQO> operationLogQOList = new ArrayList<>();
        OperationLogQO operationLogQO1 = new OperationLogQO(1, "sku111", "batch1", "colour", "red");
        OperationLogQO operationLogQO2 = new OperationLogQO(1, "sku111", "batch1", "stock", "soldout");
        OperationLogQO operationLogQO3 = new OperationLogQO(1, "sku111", "batch1", "size", "8cm");

        operationLogQOList.add(operationLogQO1);
        operationLogQOList.add(operationLogQO2);
        operationLogQOList.add(operationLogQO3);

        Map<String, List<OperationLogQO>> m = operationLogQOList.stream()
                .collect(Collectors.groupingBy(OperationLogQO::getBatchNo));

        System.out.println(m);
    }
}
