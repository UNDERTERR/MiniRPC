package org.example.remoting.transport.netty.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.example.compress.Compress;
import org.example.enums.CompressTypeEnum;
import org.example.enums.SerializationTypeEnum;
import org.example.extension.ExtensionLoader;
import org.example.remoting.constants.RpcConstants;
import org.example.remoting.dto.RpcMessage;
import org.example.serialize.Serializer;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class RpcMessageEncoder extends MessageToByteEncoder<RpcMessage> {
    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    /**
     *
     * @param ctx
     * @param msg
     * @param out
     * 1.写魔数、版本、预留长
     * 2.写入 messageType（心跳请求/响应）、序列化类型、压缩类型、请求ID
     * 3.没有 bodyBytes，不做序列化、不写消息体
     * 4.回填 fullLength（其实就是 header 长度）
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcMessage msg, ByteBuf out) {
        try {
            //[魔数][版本][预留 fullLength][类型][编码方式][压缩方式][请求 ID][可选 body]
            out.writeBytes(RpcConstants.MAGIC_NUMBER);
            out.writeByte(RpcConstants.VERSION);
            out.writerIndex(out.writerIndex() + 4);

            byte messageType = msg.getMessageType();

            out.writeByte(messageType);
            out.writeByte(msg.getCodec());
            out.writeByte(CompressTypeEnum.GZIP.getCode());
            out.writeInt(ATOMIC_INTEGER.getAndIncrement());

            byte[] bodyBytes = null;
            int fullLenth = RpcConstants.HEAD_LENGTH;
            if (messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
                //序列化类型
                String codecName = SerializationTypeEnum.getName(msg.getCodec());
                log.info("codec name:[{}]", codecName);
                //通过传入的msg层层获取到序列化协议类型
                Serializer serializer = ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(codecName);

                bodyBytes = serializer.serialize(msg.getData());
                String compressName = SerializationTypeEnum.getName(msg.getCompress());
                Compress compress = ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
                bodyBytes = compress.compress(bodyBytes);
                fullLenth = bodyBytes.length;
            }
            if(bodyBytes != null) {
                out.writeBytes(bodyBytes);
            }
            int writeIndex = out.writerIndex();
            out.writerIndex(writeIndex-fullLenth + RpcConstants.HEAD_LENGTH +1);
            out.writeInt(fullLenth);
            out.writerIndex(writeIndex);
        } catch (Exception e) {
            log.error("encode is error!", e);
        }

    }
}
