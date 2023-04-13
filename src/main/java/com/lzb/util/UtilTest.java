package com.lzb.util;

import java.util.*;

public class UtilTest {
    private static class Node<E> {
        E item;
    }

    public static void main(String[] args) {
        // List 是接口， ArrayList  LinkedList 都实现了这个接口
        List<String> list = new ArrayList<>(1);
        list.add("Hello");
        list.add("World");
        list.add("Ahahaha");

        Collections.sort(list);  // 字母排序

        // For-Each 遍历 List
        for (var str : list) {
            System.out.println(str);
        }

        // 获取迭代器
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }


        list = new LinkedList<>();
        list.add("a");
        list.add("b");
        list.add("c");

        for (var s : list) {
            System.out.println(s);
        }

        // Map 是一个接口
        Map<String, String> map = new HashMap<>();
        map.put("1", "value1");
        map.put("2", "value2");
        map.put("3", "value3");

        for (var entry : map.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
        }

        // 有序map, 通过维护一个运行于所有条目的双向链表，LinkedHashMap保证了元素迭代的顺序
        // HashMap+LinkedList
        map = new LinkedHashMap<>();
        map.put("4", "v4");
        for (var entry : map.entrySet()) {
            System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
        }
    }
}
