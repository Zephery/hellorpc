package com.hellorpc.common;

/**
 * @author wenzhihuai
 * @since 2018/8/17 15:49
 */
public interface ServiceRegistry {

    void register(String serviceName, String serviceAddress);

    String discover(String name);

}
