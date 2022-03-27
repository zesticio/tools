package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import com.zestic.buffer.Buffer;

abstract public class AbstractBufferCodec<T extends Buffer> extends VariableCodec<T> {

    public void encode(T value, DataOutput dataOut) throws IOException {
        dataOut.writeInt(value.length);
        dataOut.write(value.data, value.offset, value.length);
    }

    public T decode(DataInput dataIn) throws IOException {
        int size = dataIn.readInt();
        byte[] data = new byte[size];
        dataIn.readFully(data);
        return createBuffer(data);
    }

    abstract protected T createBuffer(byte [] data);
    
    public T deepCopy(T source) {
        return createBuffer(source.deepCopy().data);
    }

    public boolean isDeepCopySupported() {
        return true;
    }

    @Override
    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(T object) {
        return object.length+4;
    }

}
