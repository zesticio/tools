package com.zestic.buffer.codec;

import com.zestic.buffer.UTF8Buffer;

public class UTF8BufferCodec extends AbstractBufferCodec<UTF8Buffer> {
    public static final UTF8BufferCodec INSTANCE = new UTF8BufferCodec();

    @Override
    protected UTF8Buffer createBuffer(byte[] data) {
        return new UTF8Buffer(data);
    }

}
