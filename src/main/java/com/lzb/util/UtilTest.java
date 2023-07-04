package com.lzb.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Method;
// import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

public class UtilTest {
    private static final ObjectMapper mapper = new ObjectMapper().
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    private static class Node<E> {
        E item;
    }

    public void Test(List<String> list) {
        System.out.println(list);
    }


    public static void main(String[] args) {
        var l4 = new ArrayList<>();
        l4.add(888888);
        l4.add(999999);
        l4.add(111111);

        int a = 1;
        int b = 2;
        int c = 1 + 2;

        System.out.println(c);

        var l3 = new java.util.ArrayList<Integer>(10) {{
            add(1111);
            add(2222);
            add(3333);
            add(4444);
        }};
//        Map<String, String> tagvalue_property_map = new HashMap<String, String>() {{
//            put("value$", "当前值");
//            put("ts$", "时间戳");
//            put("qs$", "质量戳");
//        }};
        try {
            Method test1 = UtilTest.class.getMethod("Test", List.class);
            String methodStr = mapper.writeValueAsString(Arrays.stream(test1.getParameterTypes()).map(Class::getName).collect(Collectors.toList()));
            List<String> l = new ArrayList<>();
            l.add("1");
            String dataStr = mapper.writeValueAsString(l);

//            String cn = lstr.getClass().getName();
//            Method test1 = UtilTest.class.getMethod("Test", List.class);
//            Class<?>[] listType = test1.getParameterTypes();

//            UtilTest test = new UtilTest();
//
//            Class<?>[] listType = test1.getParameterTypes();
//            test.getClass().getMethod("Test", listType).invoke(test1, lstr);
//
//
            String metaId = mapper.convertValue(methodStr, String.class);
            Class<?> aClass = UtilTest.class.getClassLoader().loadClass(metaId);
            UtilTest test = new UtilTest();

            //test.getClass().getMethod("Test", List.class).invoke(test, lstr);


        } catch (Exception e) {
            e.printStackTrace();
        }


        var l = new java.util.ArrayList<Integer>(10) {{
            add(1111);
            add(2222);
            add(3333);
            add(4444);
        }};

        var l2 = new java.util.ArrayList<Integer>(10) {{
            add(1111);
            add(2222);
            add(3333);
            add(4444);
        }};
        String lName = l.getClass().getName();
        String l2Name = l2.getClass().getName();


        l.add(1);
        l.add(0);
        l.add(2);
        l.set(4, 5);
        l.remove(Integer.valueOf(0));
        l.remove(1);
        for (var l1 : l) {
            System.out.println(l1);
        }

        // List 是接口， ArrayList  LinkedList 都实现了这个接口
        List<String> list = new ArrayList<>(1);
        list.add("Hello");
        list.add("World");
        list.add("Ahahaha");
        list.size();

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
