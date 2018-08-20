package com.hellorpc.client;

import com.hellorpc.common.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author wenzhihuai
 * @since 2018/8/18 14:54
 */
@Controller
public class HelloController {
    @Resource
    private ApplicationContext context;

    @GetMapping("/etcd")
    @ResponseBody
    public String sayhello() {
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        HelloService helloService = rpcProxy.create(HelloService.class);
        return helloService.say("jofajoifjwoefwoiefjaowjfoawjfoiwjofjowj");
    }
}
