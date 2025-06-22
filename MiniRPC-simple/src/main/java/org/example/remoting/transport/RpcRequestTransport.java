package org.example.remoting.transport;

import org.example.extension.SPI;
import org.example.remoting.dto.RpcRequest;

@SPI
public interface RpcRequestTransport {
    Object sendRpcRequest(RpcRequest rpcRequest);
}
