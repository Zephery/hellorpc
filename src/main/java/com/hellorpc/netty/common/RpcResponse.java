package com.hellorpc.netty.common;

import lombok.Data;

/**
 * 封装 RPC 响应
 *
 * @author huangyong
 * @since 1.0.0
 */
@Data
public class RpcResponse {

    private String requestId;
    private Exception exception;
    private Object result;

    public boolean hasException() {
        return exception != null;
    }
}
