package org.example.loadbalance.loadbalancer;

import org.example.loadbalance.AbstractLoadBalance;
import org.example.loadbalance.LoadBalance;
import org.example.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random = new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}
