package com.lzb.event;

import lombok.Data;
import org.springframework.context.ApplicationEvent;

/**
 * 定义一个事件（MyTestEvent ）,需要继承spring的ApplicationEvent
 */
@Data
public class MyTestEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;

    private String msg ;

    public MyTestEvent(Object source,String msg) {
        super(source);
        this.msg = msg;
    }
}
