package org.example.spring;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;

/**
 这个类的作用就是在指定的包路径中扫描符合条件的类（如带有某个注解的类），并将其注册到 Spring 容器中。
 Class<? extends Annotation> annoType 我们要扫描的带某个注解的注解类
 */
public class CustomScanner extends ClassPathBeanDefinitionScanner {
    public CustomScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annoType) {
        super(registry);
        addIncludeFilter(new AnnotationTypeFilter(annoType));
    }
    @Override
    public int scan(String... basePackages) {
        return super.scan(basePackages);
    }
}
/*
    BeanDefinition它描述了Bean的元数据，包括类名、是否为抽象类、构造函数和属性值等相关信息，这些元数据是告诉Spring如何创建和初始化相对应的Bean。
 */
