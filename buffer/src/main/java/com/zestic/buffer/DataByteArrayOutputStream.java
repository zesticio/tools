package com.zestic.buffer;

import java.io.DataOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UTFDataFormatException;

public class DataByteArrayOutputStream extends OutputStream implements DataOutput {
    private static final int DEFAULT_SIZE = 2048;
    protected byte buf[];
    protected int pos;

    protected AbstractVarIntSupport helper = new AbstractVarIntSupport() {
        @Override
        protected byte readByte() {
            throw new UnsupportedOperationException();
        }

        @Override
        protected void writeByte(int value) throws IOException {
            DataByteArrayOutputStream.this.writeByte(value);
        }
    };

    public DataByteArrayOutputStream(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        buf = new byte[size];
    }

    public DataByteArrayOutputStream(byte buf[]) {
        if (buf == null || buf.length == 0) {
            throw new IllegalArgumentException("Invalid buffer");
        }
        this.buf = buf;
    }

    public DataByteArrayOutputStream() {
        this(DEFAULT_SIZE);
    }

    public void restart(int size) {
        buf = new byte[size];
        pos = 0;
    }

    /*
     * start using a fresh byte array
     */
    public void restart() {
        restart(DEFAULT_SIZE);
    }

    /*
     * Get a Buffer from the stream
     *
     * @return the byte sequence
     */
    public Buffer toBuffer() {
        return new Buffer(buf, 0, pos);
    }

    /*
     * Writes the specified byte to this byte array output stream.
     *
     * @param b the byte to be written.
     * @throws IOException
     */
    public void write(int b) throws IOException {
        int newcount = pos + 1;
        ensureEnoughBuffer(newcount);
        buf[pos] = (byte) b;
        pos = newcount;
        onWrite();
    }

    public void write(Buffer data) throws IOException {
        write(data.data, data.offset, data.length);
    }

    /*
     * Writes <code>len</code> bytes from the specified byte array starting at
     * offset <code>off</code> to this byte array output stream.
     *
     * @param b the data.
     * @param off the start offset in the data.
     * @param len the number of bytes to write.
     * @throws IOException
     */
    public void write(byte b[], int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        int newcount = pos + len;
        ensureEnoughBuffer(newcount);
        System.arraycopy(b, off, buf, pos, len);
        pos = newcount;
        onWrite();
    }

    /*
     * @return the underlying byte[] buffer
     */
    public byte[] getData() {
        return buf;
    }

    /*
     * reset the output stream
     */
    public void reset() {
        pos = 0;
    }

    /*
     * Set the current position for writing
     *
     * @param offset
     * @throws IOException
     */
    public void position(int offset) throws IOException {
        ensureEnoughBuffer(offset);
        pos = offset;
        onWrite();
    }

    public int position() {
        return pos;
    }

    public int size() {
        return pos;
    }

    public void writeBoolean(boolean v) throws IOException {
        ensureEnoughBuffer(pos + 1);
        buf[pos++] = (byte) (v ? 1 : 0);
        onWrite();
    }

    public void writeByte(int v) throws IOException {
        ensureEnoughBuffer(pos + 1);
        buf[pos++] = (byte) (v >>> 0);
        onWrite();
    }

    public void writeShort(int v) throws IOException {
        ensureEnoughBuffer(pos + 2);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
        onWrite();
    }

    public void writeChar(int v) throws IOException {
        ensureEnoughBuffer(pos + 2);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
        onWrite();
    }

    public void writeInt(int v) throws IOException {
        ensureEnoughBuffer(pos + 4);
        buf[pos++] = (byte) (v >>> 24);
        buf[pos++] = (byte) (v >>> 16);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
        onWrite();
    }

    public void writeLong(long v) throws IOException {
        ensureEnoughBuffer(pos + 8);
        buf[pos++] = (byte) (v >>> 56);
        buf[pos++] = (byte) (v >>> 48);
        buf[pos++] = (byte) (v >>> 40);
        buf[pos++] = (byte) (v >>> 32);
        buf[pos++] = (byte) (v >>> 24);
        buf[pos++] = (byte) (v >>> 16);
        buf[pos++] = (byte) (v >>> 8);
        buf[pos++] = (byte) (v >>> 0);
        onWrite();
    }

    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public void writeBytes(String s) throws IOException {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            write((byte) s.charAt(i));
        }
    }

    public void writeChars(String s) throws IOException {
        int length = s.length();
        for (int i = 0; i < length; i++) {
            int c = s.charAt(i);
            write((c >>> 8) & 0xFF);
            write((c >>> 0) & 0xFF);
        }
    }

    public void writeUTF(String str) throws IOException {
        int strlen = str.length();
        int encodedsize = 0;
        int c;
        for (int i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                encodedsize++;
            } else if (c > 0x07FF) {
                encodedsize += 3;
            } else {
                encodedsize += 2;
            }
        }
        if (encodedsize > 65535) {
            throw new UTFDataFormatException("encoded string too long: " + encodedsize + " bytes");
        }
        ensureEnoughBuffer(pos + encodedsize + 2);
        writeShort(encodedsize);
        int i = 0;
        for (i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) {
                break;
            }
            buf[pos++] = (byte) c;
        }
        for (; i < strlen; i++) {
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                buf[pos++] = (byte) c;
            } else if (c > 0x07FF) {
                buf[pos++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                buf[pos++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                buf[pos++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            } else {
                buf[pos++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                buf[pos++] = (byte) (0x80 | ((c >> 0) & 0x3F));
            }
        }
        onWrite();
    }

    private void ensureEnoughBuffer(int newcount) {
        if (newcount > buf.length) {
            resize(newcount);
        }
    }

    protected void resize(int newcount) {
        byte newbuf[] = new byte[Math.max(buf.length << 1, newcount)];
        System.arraycopy(buf, 0, newbuf, 0, pos);
        buf = newbuf;
    }

    /*
     * This method is called after each write to the buffer.  This should allow subclasses 
     * to take some action based on the writes, for example flushing data to an external system based on size. 
     */
    protected void onWrite() throws IOException {
    }

    public void skip(int size) throws IOException {
        ensureEnoughBuffer(pos + size);
        pos += size;
        onWrite();
    }

    public void writeVarInt(int value) throws IOException {
        helper.writeVarInt(value);
    }

    public void writeVarLong(long value) throws IOException {
        helper.writeVarLong(value);
    }

    public void writeVarSignedInt(int value) throws IOException {
        helper.writeVarSignedInt(value);
    }

    public void writeVarSignedLong(long value) throws IOException {
        helper.writeVarSignedLong(value);
    }
}
