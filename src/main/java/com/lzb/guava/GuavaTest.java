package com.lzb.guava;

import com.google.common.base.*;
import com.google.common.base.Optional;
import com.google.common.collect.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GuavaTest {
    public static void main(String args[]) {
        Integer count = 10;
        Preconditions.checkArgument(count > 0, "must be positive: %s", count);

        // 空白回车换行对应换成一个#，一对一换
        String stringWithLinebreaks = "hello world\r\r\ryou are here\n\ntake it\t\t\teasy";
        String s6 = CharMatcher.BREAKING_WHITESPACE.replaceFrom(stringWithLinebreaks, '#');
        System.out.println(s6); // hello#world###you#are#here##take#it###easy
        // 将所有连在一起的空白回车换行字符换成一个#，倒塌
        String tabString = "  hello   \n\t\tworld   you\r\nare             here  ";
        String tabRet = CharMatcher.WHITESPACE.collapseFrom(tabString, '#');
        System.out.println(tabRet); // #hello#world#you#are#here#


        System.out.println(Strings.isNullOrEmpty("")); // true
        System.out.println(Strings.isNullOrEmpty(null)); // true
        System.out.println(Strings.isNullOrEmpty("hello")); // false
        // 将null转化为""
        System.out.println(Strings.nullToEmpty(null)); // ""

        // 从尾部不断补充T只到总共8个字符，如果源字符串已经达到或操作，则原样返回。类似的有padStart
        System.out.println(Strings.padEnd("hello", 8, 'T')); // helloTTT


        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(new File("./tmp.txt"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        List<Date> dateList = new ArrayList<Date>();
        dateList.add(new Date());
        dateList.add(null);
        dateList.add(new Date());
        // 构造连接器：如果有null元素，替换为no string
        Joiner joiner2 = Joiner.on("#").useForNull("no string");
        try {
            // 将list的元素的tostring()写到fileWriter，是否覆盖取决于fileWriter的打开方式，默认是覆盖，若有true，则是追加
            joiner2.appendTo(fileWriter, dateList);
            // 必须添加close()，否则不会写文件
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        StringBuilder stringBuilder = new StringBuilder("hello");
        // 字符串连接器，以|为分隔符，同时去掉null元素
        Joiner joiner1 = Joiner.on("|").skipNulls();
        // 构成一个字符串foo|bar|baz并添加到stringBuilder
        stringBuilder = joiner1.appendTo(stringBuilder, "foo", "bar", null, "baz");
        System.out.println(stringBuilder); // hellofoo|bar|baz

        // 分割符为|，并去掉得到元素的前后空白
        Splitter sp = Splitter.on("|").trimResults();
        String str = "hello | world | your | Name ";
        Iterable<String> ss = sp.split(str);
        for (String it : ss) {
            System.out.println(it);
        }


        // 普通Collection的创建
        List<String> list = Lists.newArrayList();
        Set<String> set = Sets.newHashSet();
        Map<String, String> map2 = Maps.newHashMap();

        // 不变Collection的创建
        ImmutableList<String> iList = ImmutableList.of("a", "b", "c");
        iList.get(1);
        ImmutableSet<String> iSet = ImmutableSet.of("e1", "e2");
        ImmutableMap<String, String> iMap = ImmutableMap.of("k1", "v1", "k2", "v2");

        Map.of(1, 2, 3, 4);

        Multimap<String, Integer> map = ArrayListMultimap.create();
        map.put("aa", 1);
        map.put("aa", 2);
        System.out.println(map.get("aa"));  //[1, 2]


        Optional<Integer> possible = Optional.of(5);
        possible.isPresent(); // returns true
        possible.get(); // returns 5

    }
}
