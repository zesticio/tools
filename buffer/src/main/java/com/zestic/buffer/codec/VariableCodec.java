package com.zestic.buffer.codec;

abstract public class VariableCodec<T> implements Codec<T> {

    public int getFixedSize() {
        return -1;
    }

    public boolean isDeepCopySupported() {
        return false;
    }

    public T deepCopy(T source) {
        throw new UnsupportedOperationException();
    }

    public boolean isEstimatedSizeSupported() {
        return false;
    }

    public int estimatedSize(T object) {
        throw new UnsupportedOperationException();
    }
}
