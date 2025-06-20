package org.example.compress.gzip;

import org.example.compress.Compress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipCompress implements Compress {
    private static final int BUFFER_SIZE = 1024*4;
    @Override
    public byte[] compress(byte[] data) {
        if(data == null)
            throw new NullPointerException("data is null");
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             GZIPOutputStream gzip = new GZIPOutputStream(out)){
            gzip.write(data);
            gzip.flush();//调用 finish() 本身就会把数据刷新并完成 GZIP 尾部的写入。这里可以不用写，只是以防万一
            gzip.finish();
            return out.toByteArray();//gzip 是包裹了 out 的一层“压缩过滤器”流,本身也不存储,工具而已
        }catch (IOException e) {
            throw new RuntimeException("gzip compress error", e);
        }
    }

    @Override
    public byte[] decompress(byte[] data) {
        if(data == null)
            throw new NullPointerException("data is null");
        try(ByteArrayOutputStream out = new ByteArrayOutputStream();
            GZIPInputStream gunzip = new GZIPInputStream(new ByteArrayInputStream(data))){
            byte[] buffer = new byte[BUFFER_SIZE];
            int n;
            while ((n = gunzip.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        }catch (IOException e) {
            throw new RuntimeException("decompress error", e);
        }
    }
}
