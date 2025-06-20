package org.example.config;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class RpcServiceConfig {

    /**
     * target service
     */
    private Object service;
    /**
     * when the interface has multiple implementation classes, distinguish by group
     */
    private String version="";
    private String group="";

    public String getRpcServiceName() {
        return this.getServiceName()+this.getVersion()+this.getGroup();
    }
    public String getServiceName() {
        return service.getClass().getInterfaces()[0].getCanonicalName();
    }
}
