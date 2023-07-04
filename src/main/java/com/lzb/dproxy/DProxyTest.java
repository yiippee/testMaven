package com.lzb.dproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

// https://www.liaoxuefeng.com/wiki/1252599548343744/1264804593397984
public class DProxyTest {
    public static void main(String[] args) {
        Hello h = new Hello() {
            @Override
            public void morning(String name) {
                System.out.println("hello " + name);
            }
        };
        h.morning("lzb");

        // 动态代理 动态代理实际上是JVM在运行期动态创建class字节码并加载的过程，它并没有什么黑魔法
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                System.out.println(method);
                if (method.getName().equals("morning")) {
                    System.out.println("Good morning, " + args[0]);
                }
                return null;
            }
        };
        Hello hello = (Hello) Proxy.newProxyInstance(
                Hello.class.getClassLoader(), // 传入ClassLoader
                new Class[]{Hello.class}, // 传入要实现的接口
                handler); // 传入处理调用方法的InvocationHandler
        hello.morning("Bob");
    }
}

interface Hello {
    void morning(String name);
}

class HelloDynamicProxy implements Hello {
    InvocationHandler handler;

    public HelloDynamicProxy(InvocationHandler handler) {
        this.handler = handler;
    }

    public void morning(String name) {
//        handler.invoke(
//                this,
//                Hello.class.getMethod("morning", String.class),
//                new Object[]{name});
    }
}