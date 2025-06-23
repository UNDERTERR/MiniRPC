package org.example.remoting.transport.netty.client;

import org.example.remoting.dto.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  是一个中间缓存组件，用于在异步 RPC 调用中，记录“哪些请求还没收到响应”，方便后续匹配响应并完成回调或返回结果。
 */
public class UnprocessedRequests {
    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }
    //当服务端的响应回来时，找到之前对应的请求，并通知（完成）那个等待结果的 CompletableFuture。
    public void complete(RpcResponse<Object> rpcResponse) {
        CompletableFuture<RpcResponse<Object>> future = UNPROCESSED_RESPONSE_FUTURES.remove(rpcResponse.getRequestId());
        if (null != future) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }
}
/*
Client
│
├── 1. 发送请求（requestId = 123）──▶ 服务端
│
├── 2. unprocessedRequests.put("123", future)
│
├── 3. 等待中……
│
◀── 4. 接收到 response（requestId = 123）从 Netty 回调
│
├── 5. 从 unprocessedRequests 中拿到 future，future.complete(response)
│
└── 6. 调用者拿到结果

 */
