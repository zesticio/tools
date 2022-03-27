package com.zestic.buffer;

import java.io.EOFException;
import java.io.IOException;
import java.io.OutputStream;

final public class BufferOutputStream extends OutputStream {

    byte buffer[];
    int offset;
    int limit;
    int pos;

    public BufferOutputStream(int size) {
        this(new byte[size]);
    }

    public BufferOutputStream(byte[] buffer) {
        this.buffer = buffer;
        this.limit = buffer.length;
    }

    public BufferOutputStream(Buffer data) {
        this.buffer = data.data;
        this.pos = this.offset = data.offset;
        this.limit = data.offset + data.length;
    }


    public void write(int b) throws IOException {
        int newPos = pos + 1;
        checkCapacity(newPos);
        buffer[pos] = (byte) b;
        pos = newPos;
    }

    public void write(byte b[], int off, int len) throws IOException {
        int newPos = pos + len;
        checkCapacity(newPos);
        System.arraycopy(b, off, buffer, pos, len);
        pos = newPos;
    }

    public Buffer getNextBuffer(int len) throws IOException {
        int newPos = pos + len;
        checkCapacity(newPos);
        return new Buffer(buffer, pos, len);
    }

    /*
     * Ensures the the buffer has at least the minimumCapacity specified.
     * @param i
     * @throws EOFException
     */
    private void checkCapacity(int minimumCapacity) throws IOException {
        if (minimumCapacity > limit) {
            throw new EOFException("Buffer limit reached.");
        }
    }

    public void reset() {
        pos = offset;
    }

    public Buffer toBuffer() {
        return new Buffer(buffer, offset, pos);
    }

    public byte[] toByteArray() {
        return toBuffer().toByteArray();
    }

    public int size() {
        return offset - pos;
    }

}
