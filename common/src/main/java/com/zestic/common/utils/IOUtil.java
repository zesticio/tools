package com.zestic.common.utils;

import java.io.*;

public class IOUtil {

    public static void copy(InputStream is, OutputStream os, long length) throws IOException {
        byte b[] = new byte[8192];
        while (length > 0) {
            int b0 = is.read();
            if (b0 < 0)
                throw new IOException("EOS");
            os.write(b0);
            --length;
            if (length == 0)
                return;
            int read = length > b.length ? b.length : (int) length;
            read = is.read(b, 0, read);
            if (read > 0) {
                os.write(b, 0, read);
                length -= read;
            }
        }
    }

    public static void readFully(InputStream is, byte[] buffer, int position, int length) throws IOException {
        while (position < length) {
            int b0 = is.read();
            if (b0 < 0)
                throw new IOException("EOS");
            buffer[position++] = (byte) b0;
            position += is.read(buffer, position, length - position);
        }
    }

    public static void readFully(InputStream is, byte[] buffer) throws IOException {
        readFully(is, buffer, 0, buffer.length);
    }

    /**
     * Reading file from resources packaged
     *
     * @param clasz
     * @param filename
     * @return
     */
    public InputStream read(Class classz, final String filename) {
        InputStream ioStream = classz.getClass()
                .getClassLoader()
                .getResourceAsStream(filename);

        if (ioStream == null) {
            throw new IllegalArgumentException(filename + " is not found");
        }
        return ioStream;
    }

    public static byte[] readFile(File file) throws IOException {
        long llen = file.length();
        if (llen > Integer.MAX_VALUE)
            throw new IOException("file " + file.getAbsolutePath() + " is too large");

        int len = (int) llen;
        byte b[] = new byte[len];
        FileInputStream fis = new FileInputStream(file);
        try {
            readFully(fis, b);
            return b;
        } finally {
            fis.close();
        }
    }

    public static void main(String[] args) {
        IOUtil instance = new IOUtil();
        InputStream is = instance.read(instance.getClass(), "demo.txt");
    }
}
