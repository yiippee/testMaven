package com.lzb.cglib;

import net.sf.cglib.proxy.Enhancer;

public class CglibTest {

    public static void main(String[] args) {

        // 通过CGLIB动态代理获取代理对象的过程
        // 创建Enhancer对象，类似于JDK动态代理的Proxy类
        Enhancer enhancer = new Enhancer();
        // 设置目标类的字节码文件
        enhancer.setSuperclass(UserDao.class);
        // 设置回调函数
        enhancer.setCallback(new LogInterceptor());
        
        // create方法正式创建代理类
        UserDao userDao = (UserDao) enhancer.create();
        // 调用代理类的具体业务方法
        userDao.findAllUsers();
        userDao.findUsernameById(1);
    }
}
