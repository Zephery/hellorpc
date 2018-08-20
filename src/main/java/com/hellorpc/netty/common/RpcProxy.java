package com.hellorpc.netty.common;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * RPC 代理（用于创建 RPC 服务代理）
 *
 * @author huangyong
 * @since 1.0.0
 */
@Slf4j
public class RpcProxy {


    private String serviceAddress;

    private ServiceDiscovery serviceDiscovery;

    public RpcProxy(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass) {
        return create(interfaceClass, "");
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<?> interfaceClass, final String serviceVersion) {
        // 创建动态代理对象
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                (proxy, method, args) -> {
                    // 创建 RPC 请求对象并设置请求属性
                    RpcRequest request = new RpcRequest();
                    request.setRequestId(UUID.randomUUID().toString());
                    request.setInterfaceName(method.getDeclaringClass().getName());
                    request.setServiceVersion(serviceVersion);
                    request.setMethodName(method.getName());
                    request.setParameterTypes(method.getParameterTypes());
                    request.setParameters(args);
                    // 获取 RPC 服务地址
                    if (serviceDiscovery != null) {
                        String serviceName = interfaceClass.getName();
                        if (!Strings.isNullOrEmpty(serviceVersion)) {
                            serviceName += "-" + serviceVersion;
                        }
                        serviceAddress = serviceDiscovery.discover(serviceName);
                    }
                    if (Strings.isNullOrEmpty(serviceAddress)) {
                        throw new RuntimeException("server address is empty");
                    }
                    // 从 RPC 服务地址中解析主机名与端口号
                    String[] array = serviceAddress.split(":");
                    String host = array[0];
                    int port = Integer.parseInt(array[1]);
                    // 创建 RPC 客户端对象并发送 RPC 请求
                    RpcClient client = new RpcClient(host, port);
                    long time = System.currentTimeMillis();
                    RpcResponse response = client.send(request);
                    if (response == null) {
                        throw new RuntimeException("response is null");
                    }
                    // 返回 RPC 响应结果
                    if (response.hasException()) {
                        throw response.getException();
                    } else {
                        return response.getResult();
                    }
                }
        );
    }
}
