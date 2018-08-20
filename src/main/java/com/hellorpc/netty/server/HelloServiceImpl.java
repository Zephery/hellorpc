package com.hellorpc.netty.server;


import com.hellorpc.netty.HelloService;

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}
