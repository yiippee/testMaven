package com.lzb.cglib;

public class UserDao {

    public void findAllUsers() {
        System.out.println("UserDao 查询所有用户");
    }

    public String findUsernameById(int id) {
        System.out.println("UserDao 根据ID查询用户");
        return "公众号：程序新视界";
    }
}
