package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntegerCodec implements Codec<Integer> {

    public static final IntegerCodec INSTANCE = new IntegerCodec();

    public void encode(Integer object, DataOutput dataOut) throws IOException {
        dataOut.writeInt(object);
    }

    public Integer decode(DataInput dataIn) throws IOException {
        return dataIn.readInt();
    }

    public int getFixedSize() {
        return 4;
    }

    public Integer deepCopy(Integer source) {
        return source;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(Integer object) {
        return 4;
    }
}
