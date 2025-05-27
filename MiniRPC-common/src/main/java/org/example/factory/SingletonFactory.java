package org.example.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//写成泛型方法，确实是为了保证传入的 Class<T> 类型和返回的对象类型 T 一致，这样就能让编译器帮你做类型检查，避免类型转换错误。
/**
    Todo:
    如果不用DCL，在两个线程同时进入 synchronized (LOCK) {}时：
    T1 获得锁，创建对象放入 OBJECT_MAP；
    然后 T2 获得锁 —— 如果不再判断一次，它会再创建一个对象，再次 put 覆盖（或报错）；
    这样就 破坏了单例 或引发线程安全问题。
 */
public final class SingletonFactory {
    private SingletonFactory() {
    }

    private static final Object LOCK = new Object();
    private static final Map<String, Object> OBJECT_MAP = new ConcurrentHashMap<String, Object>();

    public static <T> T getInstance(final Class<T> c) {
        if (c == null) {
            throw new IllegalArgumentException();
        }
        String key = c.toString();
        if (OBJECT_MAP.containsKey(key)) {
            return c.cast(OBJECT_MAP.get(key));
        } else {
            synchronized (LOCK) {
                if (OBJECT_MAP.containsKey(key)) {
                    return c.cast(OBJECT_MAP.get(key));
                } else {
                    try {
                        T instance = c.getDeclaredConstructor().newInstance();
                        OBJECT_MAP.put(key, instance);
                        return instance;
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }
            }
        }
    }

}

