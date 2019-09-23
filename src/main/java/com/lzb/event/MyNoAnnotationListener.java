package com.lzb.event;

import lombok.Data;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 定义一下监听器，自己定义的监听器需要实现ApplicationListener，
 * 同时泛型参数要加上自己要监听的事件Class名，在重写的方法onApplicationEvent中，添加自己的业务处理
 */
@Data
@Component
public class MyNoAnnotationListener implements ApplicationListener<MyTestEvent> {
    @Override
    public void onApplicationEvent(MyTestEvent event) {
        System.out.println("非注解监听器：" + event.getMsg());
    }
}
