package com.lzb.checkEx;

import com.dyt.fakeWarehouse.checkExist.*;

public class CheckEx {
    public static void main(String[] args) {
        try {
            CheckExistTest.mainTest(args);
        } catch (NoClassDefFoundError e) {
            e.printStackTrace();
        }
        System.out.println("...");
    }
}
