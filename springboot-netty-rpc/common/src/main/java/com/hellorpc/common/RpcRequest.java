package com.hellorpc.common;

import lombok.Data;

/**
 * 封装 RPC 请求
 *
 * @author huangyong
 * @since 1.0.0
 */
@Data
public class RpcRequest {

    private String requestId;
    private String interfaceName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
}
