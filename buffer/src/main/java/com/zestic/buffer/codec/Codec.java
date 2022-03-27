package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Codec<T> {

    void encode(T object, DataOutput dataOut) throws IOException;

    T decode(DataInput dataIn) throws IOException;

    int getFixedSize();

    boolean isEstimatedSizeSupported();

    int estimatedSize(T object);

    boolean isDeepCopySupported();

    T deepCopy(T source);
}
