package com.lzb.hutool;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;

import java.sql.SQLException;

public class DBTest {
    public static void main(String[] args) {
        DBTest t = new DBTest();
    }
    public DBTest() {
        try {
            Db.use().insert(
                    Entity.create("userinfo")
                            .set("username", "forward")
                            .set("departname", "123456")
            );
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
