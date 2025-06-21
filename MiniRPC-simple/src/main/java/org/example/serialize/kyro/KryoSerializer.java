package org.example.serialize.kyro;


import com.esotericsoftware.kryo.Kryo;
import org.example.annotaion.RpcService;
import org.example.serialize.Serializer;

/**
 *  only compatible with Java language
 *  TODO
 */
public class KryoSerializer implements Serializer {

    private final ThreadLocal<Kryo> kryoThreadLocal =ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        return kryo;
    });
    /**
     * Because Kryo is not thread safe. So, use ThreadLocal to store Kryo objects
     */
    @Override
    public byte[] serialize(Object obj) {
        return null;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }

    public class SerializeException extends RuntimeException {
        public SerializeException(String message) {
            super(message);
        }
        public SerializeException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
