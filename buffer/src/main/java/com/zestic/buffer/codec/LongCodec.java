package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongCodec implements Codec<Long> {
    
    public static final LongCodec INSTANCE = new LongCodec();
    
    public void encode(Long object, DataOutput dataOut) throws IOException {
        dataOut.writeLong(object);
    }

    public Long decode(DataInput dataIn) throws IOException {
        return dataIn.readLong();
    }

    public int getFixedSize() {
        return 8;
    }

    public Long deepCopy(Long source) {
        return source;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(Long object) {
        return 8;
    }
}
