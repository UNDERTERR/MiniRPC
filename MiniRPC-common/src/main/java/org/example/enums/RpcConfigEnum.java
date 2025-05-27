package org.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {
    RPC_CONFIG_ENUM("rpc.properties"),
    ZK_ADDRESS("rpc.zookeeper.address");
    private final String propertyValue;
}
