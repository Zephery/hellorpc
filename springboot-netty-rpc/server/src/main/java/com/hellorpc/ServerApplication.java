package com.hellorpc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

/**
 * @author wenzhihuai
 * @since 2018/8/17 18:08
 */
@Controller
@SpringBootApplication
public class ServerApplication {
    public static void main(String args[]) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
