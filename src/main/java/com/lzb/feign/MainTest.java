package com.lzb.feign;

import feign.Feign;
import feign.Request;
import feign.Retryer;

public class MainTest {
    public static void main(String[] args) {
        RemoteService service = Feign.builder()
                .options(new Request.Options(1000, 3500))
                .retryer(new Retryer.Default(5000, 5000, 3))
                .target(RemoteService.class, "http://127.0.0.1:8085");

        service.getOwner("lizhanbin");
    }
}
