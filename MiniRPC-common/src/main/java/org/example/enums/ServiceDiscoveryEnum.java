package org.example.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.A;

@AllArgsConstructor
@Getter
public enum ServiceDiscoveryEnum {
    ZK("zk");
    private final String name;
}
