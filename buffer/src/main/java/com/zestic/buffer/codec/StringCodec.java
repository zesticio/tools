package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringCodec implements Codec<String> {

    public static final StringCodec INSTANCE = new StringCodec();

    public void encode(String object, DataOutput dataOut) throws IOException {
        dataOut.writeUTF(object);
    }

    public String decode(DataInput dataIn) throws IOException {
        return dataIn.readUTF();
    }

    public int getFixedSize() {
        return -1;
    }

    public String deepCopy(String source) {
        return source;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(String object) {
        return object.length() + 2;
    }
}
