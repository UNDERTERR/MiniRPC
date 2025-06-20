package org.example.annotaion;

import java.lang.annotation.*;
import java.lang.reflect.Field;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {
    String version() default "";
    String group() default "";
}
