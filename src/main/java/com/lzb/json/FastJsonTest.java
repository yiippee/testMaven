package com.lzb.json;

import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FastJsonTest {
    //final String content = "{\"records\":[{\"id\":\"60\",\"name\":\"Rolex Watch\",\"description\":\"Luxury watch.\",\"price\":\"25000\",\"category_id\":\"1\",\"category_name\":\"Fashion\"},{\"id\":\"48\",\"name\":\"Bristol Shoes\",\"description\":\"Awesome shoes.\",\"price\":\"999\",\"category_id\":\"5\",\"category_name\":\"Movies\"},{\"id\":\"42\",\"name\":\"Nike Shoes for Men\",\"description\":\"Nike Shoes\",\"price\":\"12999\",\"category_id\":\"3\",\"category_name\":\"Motors\"}]}";

    public Integer id;
    public String name;
    public String description;
    public Integer price;
    public Integer category_id;
    public String category_name;

    public FastJsonTest() {
    }

    public FastJsonTest(Integer id, String name, String description, Integer price, Integer category_id, String category_name) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category_id = category_id;
        this.category_name = category_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public class RootObject {
        public List<FastJsonTest> records;
    }

    public static void main(String[] args) throws IOException {
        // RootObject productsRoot = JsonConvert.DeserializeObject < RootObject > (content);
        FastJsonTest fastJsonTest = new FastJsonTest(123, "name", "desc", 456, 7, "category");
        String ret = JsonUtil.objectToString(fastJsonTest);
        System.out.println(ret);

        FastJsonTest ft = JsonUtil.stringToObject(ret, FastJsonTest.class);
        System.out.println(ft);

        // list
        List<FastJsonTest> fts = new ArrayList<>();
        fts.add(fastJsonTest);
        fts.add(fastJsonTest);

        ret = JsonUtil.objectToString(fts);
        System.out.println(ret);

        fts = null;
        fts = JsonUtil.stringToList(ret, FastJsonTest.class);

        List<FastJsonTest> ftList = JsonUtil.convertValue(ret,
                new TypeReference<List<FastJsonTest>>() {
                });
    }
}
