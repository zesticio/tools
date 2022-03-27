package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.zestic.buffer.Buffer;

public class FixedBufferCodec implements Codec<Buffer> {

    private final int size;

    public FixedBufferCodec(int size) {
        this.size = size;
    }

    public void encode(Buffer value, DataOutput dataOut) throws IOException {
        dataOut.write(value.data, value.offset, size);
    }

    public Buffer decode(DataInput dataIn) throws IOException {
        byte[] data = new byte[size];
        dataIn.readFully(data);
        return new Buffer(data);
    }

    public int getFixedSize() {
        return size;
    }

    public Buffer deepCopy(Buffer source) {
        return source.deepCopy();
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(Buffer object) {
        return size;
    }

}
