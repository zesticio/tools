package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class VarSignedIntegerCodec extends VarIntegerCodec {

    public static final VarSignedIntegerCodec INSTANCE = new VarSignedIntegerCodec();


    public void encode(Integer value, DataOutput dataOut) throws IOException {
        super.encode(encodeZigZag(value), dataOut);
    }

    public Integer decode(DataInput dataIn) throws IOException {
        return decodeZigZag(super.decode(dataIn));
    }

    private static int decodeZigZag(int n) {
        return (n >>> 1) ^ -(n & 1);
    }

    private static int encodeZigZag(int n) {
        return (n << 1) ^ (n >> 31);
    }

    public int estimatedSize(Integer value) {
        return super.estimatedSize(encodeZigZag(value));
    }
}
