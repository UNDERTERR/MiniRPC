package org.example.serialize.hessian;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.example.exception.SerializeException;
import org.example.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class HessianSerializer implements Serializer {

    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianSerializer = new HessianOutput(byteArrayOutputStream);
            hessianSerializer.writeObject(obj);
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new SerializeException("serialize error!");
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            HessianInput hessianDeserializer= new HessianInput(byteArrayInputStream);
            Object object = hessianDeserializer.readObject();
            return clazz.cast(object);
        }catch (Exception e) {
            throw new SerializeException("deserialize error!");
        }
    }
}
