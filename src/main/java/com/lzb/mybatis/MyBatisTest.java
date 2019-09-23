package com.lzb.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyBatisTest {
    public static void main(String[] args) {
        String resource = "E:\\testMaven\\src\\main\\java\\com\\lzb\\mybatis\\mybatis-config.xml";
        File file = new File(resource);
        System.out.println(file.exists());

        try {
            // Reader reader = new FileReader(resource);

            InputStream inputStream = new FileInputStream(file);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            SqlSession sqlSession = sqlSessionFactory.openSession();
            List list = sqlSession.selectList("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
