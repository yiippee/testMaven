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
        String str = "[{\"name\": 123}]";
        JSONArray jsonArray = JSONUtil.parseArray(str);
        List<Name> nameList = jsonArray.toList(Name.class);
        for (Name name: nameList) {
            name.name = 888;
        }
        System.out.println(nameList);

        String str2 = "{\"name\": 123}";
        JSONObject j = JSONUtil.parseObj(str2);
        Name n = j.toBean(Name.class);
    }
}
