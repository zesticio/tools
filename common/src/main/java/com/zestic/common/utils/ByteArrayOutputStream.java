package com.zestic.common.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;

public class ByteArrayOutputStream extends OutputStream {

    private LinkedList<byte[]> ll;
    private final int bufferSize;
    private byte[] buffer;
    private int pos;

    public ByteArrayOutputStream(int bufferSize) {
        this.bufferSize = bufferSize;
        buffer = new byte[bufferSize];
    }

    public ByteArrayOutputStream() {
        this(32768);
    }

    public void write(int b) {
        if (pos == bufferSize)
            alloc();
        buffer[pos++] = (byte) b;
    }

    private void alloc() {
        if (ll == null)
            ll = new LinkedList<byte[]>();
        ll.addLast(buffer);
        buffer = new byte[bufferSize];
        pos = 0;
    }

    public void write(byte[] b, int off, int len) {
        while (pos + len > bufferSize) {
            // TODO insert an over size chunk instead of many small chunks
            // also track an over size sum to fix the size()
            if (pos == bufferSize)
                alloc();
            int room = bufferSize - pos;
            if (room > len)
                room = len;
            System.arraycopy(b, off, buffer, pos, room);
            pos += room;
            off += room;
            len -= room;
        }
        if (len == 0)
            return;
        System.arraycopy(b, off, buffer, pos, len);
        pos += len;
    }

    public void writeTo(OutputStream os) throws IOException {
        if (ll != null) {
            for (Iterator<byte[]> i = ll.iterator(); i.hasNext(); ) {
                byte b[] = i.next();
                os.write(b, 0, b.length);
            }
        }
        if (pos > 0)
            os.write(buffer, 0, pos);
    }

    public int size() {
        if (ll == null)
            return pos;
        // TODO with over size chunks add also the over size sum
        return ll.size() * bufferSize + pos;
    }

    public byte[] toByteArray() {
        byte out[] = new byte[size()];
        int p = 0;
        if (ll != null) {
            for (Iterator<byte[]> i = ll.iterator(); i.hasNext(); ) {
                byte b[] = i.next();
                System.arraycopy(b, 0, out, p, bufferSize);
                p += bufferSize;
            }
        }
        System.arraycopy(buffer, 0, out, p, pos);
        return out;
    }

    public String toString() {
        char chs[] = new char[size()];
        int p = 0;
        if (ll != null) {
            for (Iterator<byte[]> j = ll.iterator(); j.hasNext(); ) {
                byte b[] = j.next();
                for (int i = 0; i < bufferSize; ++i) {
                    chs[p++] = (char) (0xff & b[i]);
                }
            }
        }
        byte b[] = buffer;
        for (int i = 0; i < pos; ++i) {
            chs[p++] = (char) (0xff & b[i]);
        }
        return new String(chs);
    }

    public void reset() {
        ll = null;
        pos = 0;
    }

    public int getBufferSize() {
        return bufferSize;
    }
}
