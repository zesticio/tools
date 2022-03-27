package com.zestic.buffer.codec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ObjectCodec<T> extends VariableCodec<T> {

    public void encode(Object object, DataOutput dataOut) throws IOException {
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        ObjectOutputStream objectOut = new ObjectOutputStream(bytesOut);
        objectOut.writeObject(object);
        objectOut.close();
        byte[] data = bytesOut.toByteArray();
        dataOut.writeInt(data.length);
        dataOut.write(data);
    }

    public T decode(DataInput dataIn) throws IOException {
        int size = dataIn.readInt();
        byte[] data = new byte[size];
        dataIn.readFully(data);
        ByteArrayInputStream bytesIn = new ByteArrayInputStream(data);
        ObjectInputStream objectIn = new ObjectInputStream(bytesIn);
        try {
            return (T) objectIn.readObject();
        } catch (ClassNotFoundException e) {
            throw createIOException(e.getMessage(), e);
        }
    }

    private static IOException createIOException(String message, Throwable cause) {
        IOException answer = new IOException(message);
        answer.initCause(cause);
        return answer;
    }

}
