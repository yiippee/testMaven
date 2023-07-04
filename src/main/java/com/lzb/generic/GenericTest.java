package com.lzb.generic;

class Test<T> {
    public T e;

    Test(T e) {
        this.e = e;
    }

    // 泛型方法需要一开始定义泛型列表，列表里面的泛型标识与类的标识不一样
    // 当然方法本身也是可以使用类的泛型标识的
    public <K, V> void test(K k, V v) {
        T a = this.e;
        System.out.println(a);
        System.out.println(k);
        System.out.println(v);
    }
}

public class GenericTest {
    public static void main(String[] args) {
        var t = new Test<>(1);
        System.out.println(t.e);

        t.test(2, 3);
    }
}
