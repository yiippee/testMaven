package com.lzb.hash;

// import lombok.var;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HashTest {
        private static Set<Class<?>> hasAutoWiredBeans =
            Collections.synchronizedSet(new HashSet<>());
    public static void main(String[] args) {
        var h = new HashMap<String, String>();
        h.put("name", "lizhanbin");
        var name = h.get("name");

        final Map<String, String> m = new ConcurrentHashMap<>();
        m.put("name", "lizhanbin");
        name = m.get("name");
        System.out.printf("name: %s", name);


    }
}
