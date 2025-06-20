package org.example.compress;


import org.example.extension.SPI;

@SPI
public interface Compress {
    byte[] compress(byte[] data);
    byte[] decompress(byte[] data);
}
