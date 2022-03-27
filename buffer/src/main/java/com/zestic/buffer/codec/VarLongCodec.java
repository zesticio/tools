package com.zestic.buffer.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.ProtocolException;

public class VarLongCodec implements Codec<Long> {

    public static final VarLongCodec INSTANCE = new VarLongCodec();

    public void encode(Long object, DataOutput dataOut) throws IOException {
        long value = object;
        while (true) {
            if ((value & ~0x7FL) == 0) {
                dataOut.writeByte((int) value);
                return;
            } else {
                dataOut.writeByte(((int) value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    public Long decode(DataInput dataIn) throws IOException {
        int shift = 0;
        long result = 0;
        while (shift < 64) {
            byte b = dataIn.readByte();
            result |= (long) (b & 0x7F) << shift;
            if ((b & 0x80) == 0)
                return result;
            shift += 7;
        }
        throw new ProtocolException("Encountered a malformed variable int");
    }

    public boolean isEstimatedSizeSupported() {
        return true;
    }

    public int estimatedSize(Long object) {
        long value = object;
        if ((value & (0xffffffffffffffffL << 7)) == 0)
            return 1;
        if ((value & (0xffffffffffffffffL << 14)) == 0)
            return 2;
        if ((value & (0xffffffffffffffffL << 21)) == 0)
            return 3;
        if ((value & (0xffffffffffffffffL << 28)) == 0)
            return 4;
        if ((value & (0xffffffffffffffffL << 35)) == 0)
            return 5;
        if ((value & (0xffffffffffffffffL << 42)) == 0)
            return 6;
        if ((value & (0xffffffffffffffffL << 49)) == 0)
            return 7;
        if ((value & (0xffffffffffffffffL << 56)) == 0)
            return 8;
        if ((value & (0xffffffffffffffffL << 63)) == 0)
            return 9;
        return 10;
    }

    public int getFixedSize() {
        return -1;
    }

    public Long deepCopy(Long source) {
        return source;
    }

    public boolean isDeepCopySupported() {
        return true;
    }

}
