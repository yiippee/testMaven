package com.lzb.hutool;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
class Name {
    public Integer name;
}
public class HutoolTest {
    public static void main(String[] args) {
        Map<Integer, Integer> m = new HashMap<>();
        Integer r = m.get(null);
        Name n = new Name();
        n.setName(r);
        Integer a = null;
        Integer b = null;
        Integer c = a + b;

        String str = "[{\"name\": 123}]";
        JSONArray jsonArray = JSONUtil.parseArray(str);
        List<Name> nameList = jsonArray.toList(Name.class);
        for (Name name: nameList) {
            name.name = 888;
        }
        System.out.println(nameList);
    }
}
