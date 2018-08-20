package com.hellorpc.server;

import com.hellorpc.common.HelloService;

/**
 * @author wenzhihuai
 * @since 2018/8/17 15:32
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
    @Override
    public String say(String word) {
        return word + "world";
    }
}
