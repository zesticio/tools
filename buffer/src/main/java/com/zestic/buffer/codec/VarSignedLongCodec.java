package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VarSignedLongCodec extends VarLongCodec {

    public static final VarSignedLongCodec INSTANCE = new VarSignedLongCodec();


    public void encode(Long value, DataOutput dataOut) throws IOException {
        super.encode(encodeZigZag(value), dataOut);
    }

    public Long decode(DataInput dataIn) throws IOException {
        return decodeZigZag(super.decode(dataIn));
    }

    private static long decodeZigZag(long n) {
        return (n >>> 1) ^ -(n & 1);
    }

    private static long encodeZigZag(long n) {
        return (n << 1) ^ (n >> 63);
    }

    public int estimatedSize(Long value) {
        return super.estimatedSize(encodeZigZag(value));
    }
}
