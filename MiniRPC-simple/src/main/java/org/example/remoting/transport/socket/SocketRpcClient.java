package org.example.remoting.transport.socket;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.remoting.dto.RpcRequest;
import org.example.remoting.transport.RpcRequestTransport;

@AllArgsConstructor
@Slf4j
public class SocketRpcClient implements RpcRequestTransport {
    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        return null;
    }
}
