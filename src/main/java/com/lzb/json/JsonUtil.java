package com.lzb.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;


/**
 * @author xiaomi
 */
public class JsonUtil {

    static Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //忽略属性为null
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //忽略多余属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static String objectToString(Object object) {

        try {
            return objectMapper.writeValueAsString(object);

        } catch (JsonProcessingException e) {
            logger.error("对象转json字符串异常", e);
        }
        return null;
    }

    public static <T> T stringToObject(String json, Class<T> object) throws IOException {
        return objectMapper.readValue(json, object);
    }

    public static <T> List<T> stringToList(String json, Class<T> object) throws IOException {
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, object);
        List<T> list = objectMapper.readValue(json, listType);
        return list;
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) throws IllegalArgumentException {
        return objectMapper.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) throws IllegalArgumentException {
        return objectMapper.convertValue(fromValue, toValueTypeRef);
    }
}
