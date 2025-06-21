package org.example.serialize.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.example.serialize.Serializer;

public class ProtostuffSerializer implements Serializer {
    private static final LinkedBuffer BUFFER = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    @Override
    public byte[] serialize(Object obj) {
        Class<?> clazz = obj.getClass();
        //是 Protostuff 框架中用于描述对象结构（字段、类型等）的接口。
        Schema schema = RuntimeSchema.getSchema(clazz);
        byte[] bytes;
        try{
            bytes = ProtostuffIOUtil.toByteArray(obj, schema, BUFFER);
        }finally{
            BUFFER.clear();
        }
        return  bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }
}
