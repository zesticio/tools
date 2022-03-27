package com.zestic.buffer.codec;

import com.zestic.buffer.Buffer;

public class BufferCodec extends AbstractBufferCodec<Buffer> {
    
    public static final BufferCodec INSTANCE = new BufferCodec();

    @Override
    protected Buffer createBuffer(byte[] data) {
        return new Buffer(data);
    }
}
