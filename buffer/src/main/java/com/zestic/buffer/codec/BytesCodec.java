package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BytesCodec implements Codec<byte[]> {

    public static final BytesCodec INSTANCE = new BytesCodec();

    public void encode(byte[] data, DataOutput dataOut) throws IOException {
        dataOut.writeInt(data.length);
        dataOut.write(data);
    }

    public byte[] decode(DataInput dataIn) throws IOException {
        int size = dataIn.readInt();
        byte[] data = new byte[size];
        dataIn.readFully(data);
        return data;
    }
    
    public int getFixedSize() {
        return -1;
    }

    public byte[] deepCopy(byte[] source) {
        byte []rc = new byte[source.length];
        System.arraycopy(source, 0, rc, 0, source.length);
        return rc;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(byte[] object) {
        return object.length+4;
    }
}
