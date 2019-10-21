package com.lzb.websocket;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class WebsocketTest {
    static Session session;

    public WebsocketTest(){
        System.out.println("WebsocketTest..");
    }

    @OnOpen
    public void onOpen(Session session){
        // 打开连接，获取session
        WebsocketTest.session = session;
        System.out.println("connect success");
//        try {
//            session.getBasicRemote().sendText("hello client...");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
    @OnClose
    public void onClose(Session session){
        System.out.println("close....");

    }
    @OnMessage // 收到消息时的回调函数。至于写数据，随时都可以写啊，只要想写都可以写
    public void onReceive(Session session,String msg){
        System.out.println("receive msg: " + msg);
        try {
            Thread.sleep(1000);
            session.getBasicRemote().sendText(msg);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            String uri = "ws://localhost:8080/c?id=888";
            System.out.println("Connecting to " + uri);
            container.connectToServer(WebsocketTest.class, URI.create(uri));
            session.getBasicRemote().sendText("hello world");
            java.io.BufferedReader r = new  java.io.BufferedReader(new java.io.InputStreamReader( System.in));
            while(true){
                // 这个是等待控制台输入退出命令的
                String line=r.readLine();
                if(line.equals("quit")) break;
                session.getBasicRemote().sendText(line);
            }

        } catch ( Exception ex) {
            ex.printStackTrace();
        }
    }
}
