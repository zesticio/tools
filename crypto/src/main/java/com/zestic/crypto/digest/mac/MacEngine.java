package com.zestic.crypto.digest.mac;

import com.zestic.core.io.IoUtil;
import com.zestic.crypto.CryptoException;

import java.io.IOException;
import java.io.InputStream;

public interface MacEngine {

    default void update(byte[] in) {
        update(in, 0, in.length);
    }

    void update(byte[] in, int inOff, int len);

    byte[] doFinal();

    void reset();

    default byte[] digest(InputStream data, int bufferLength) {
        if (bufferLength < 1) {
            bufferLength = IoUtil.DEFAULT_BUFFER_SIZE;
        }
        final byte[] buffer = new byte[bufferLength];
        byte[] result;
        try {
            int read = data.read(buffer, 0, bufferLength);
            while (read > -1) {
                update(buffer, 0, read);
                read = data.read(buffer, 0, bufferLength);
            }
            result = doFinal();
        } catch (IOException e) {
            throw new CryptoException(e);
        } finally {
            reset();
        }
        return result;
    }

    int getMacLength();

    String getAlgorithm();
}
