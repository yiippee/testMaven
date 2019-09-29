package com.lzb.feign;

import feign.Param;
import feign.RequestLine;

public interface RemoteService {
    @RequestLine("GET /user/{name}")
    String getOwner(@Param(value = "name") String name);
}
