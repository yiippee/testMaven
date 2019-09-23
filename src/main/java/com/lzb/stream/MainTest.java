package com.lzb.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainTest {
    public static void main(String[] args) {
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
