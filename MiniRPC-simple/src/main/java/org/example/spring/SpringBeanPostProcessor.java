package org.example.spring;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.example.annotaion.RpcReference;
import org.example.annotaion.RpcService;
import org.example.config.RpcServiceConfig;
import org.example.enums.RpcRequestTransportEnum;
import org.example.extension.ExtensionLoader;
import org.example.factory.SingletonFactory;
import org.example.provider.ServiceProvider;
import org.example.provider.impl.ZkServiceProviderImpl;
import org.example.proxy.RpcClientProxy;
import org.example.remoting.transport.RpcRequestTransport;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;


/**
 * 只要实现这个接口，Spring 在 每个 Bean 被创建时都会自动回调这两个方法，让你有机会修改、包装、处理它们。
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor{
    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension(RpcRequestTransportEnum.NETTY.getName());
    }
    @SneakyThrows
    @Override
    //在 Spring 初始化每个 Bean 之前，如果这个 Bean 带有 @RpcService 注解，就把它注册到注册中心（如 Zookeeper），使其能被远程调用。
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] is annotated with  [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // get RpcService annotation
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // build RpcServiceProperties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean).build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if (rpcReference != null) {
                RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version()).build();
                RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient, rpcServiceConfig);
                Object clientProxy = rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}
