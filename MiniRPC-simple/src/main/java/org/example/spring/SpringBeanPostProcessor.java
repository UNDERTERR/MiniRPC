package org.example.spring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;


/**
 * call this method before creating the bean to see if the class is annotated
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor{

}
