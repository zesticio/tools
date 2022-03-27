package com.zestic.buffer.codec;

import com.zestic.buffer.AsciiBuffer;

public class AsciiBufferCodec extends AbstractBufferCodec<AsciiBuffer> {
    public static final AsciiBufferCodec INSTANCE = new AsciiBufferCodec();

    @Override
    protected AsciiBuffer createBuffer(byte[] data) {
        return new AsciiBuffer(data);
    }

}
