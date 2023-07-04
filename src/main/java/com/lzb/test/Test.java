package com.lzb.test;
//
//import cn.hutool.core.convert.Convert;
//import io.undertow.Undertow;
//import io.undertow.server.HttpHandler;
//import io.undertow.server.HttpServerExchange;
//import io.undertow.util.Headers;
//
//import java.security.MessageDigest;
//
//public class Test {
//    public static void main(String []args) {
//        int a = 1;
//        //aStr为"1"
//        String aStr = Convert.toStr(a);
//
//        long[] b = {1,2,3,4,5};
//        //bStr为："[1, 2, 3, 4, 5]"
//        String bStr = Convert.toStr(b);
//
//        Undertow server = Undertow.builder()
//                .addHttpListener(8080, "localhost")
//                .setHandler(new HttpHandler() {
//                    @Override
//                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
//                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
//                        exchange.getResponseSender().send("Hello World 3");
//                    }
//                }).build();
//        server.start();
//    }
//}
//
//class Md5Utils {
//    public Md5Utils() {
//    }
//
//    public static String MD5(String param, String salt) {
//        return MD5(param + salt);
//    }
//
//    public static String MD5(String s) {
//        char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
//
//        try {
//            byte[] btInput = s.getBytes();
//            MessageDigest mdInst = MessageDigest.getInstance("MD5");
//            mdInst.update(btInput);
//            byte[] md = mdInst.digest();
//            int j = md.length;
//            char[] str = new char[j * 2];
//            int k = 0;
//            byte[] var8 = md;
//            int var9 = md.length;
//
//            for(int var10 = 0; var10 < var9; ++var10) {
//                byte byte0 = var8[var10];
//                str[k++] = hexDigits[byte0 >>> 4 & 15];
//                str[k++] = hexDigits[byte0 & 15];
//            }
//
//            return new String(str);
//        } catch (Exception var12) {
//            return null;
//        }
//    }
//}