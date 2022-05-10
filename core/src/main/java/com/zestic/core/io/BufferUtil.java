package com.zestic.core.io;

import com.zestic.core.util.CharsetUtil;
import com.zestic.core.util.StrUtil;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

public class BufferUtil {

    public static ByteBuffer copy(ByteBuffer src, int start, int end) {
        return copy(src, ByteBuffer.allocate(end - start));
    }

    public static ByteBuffer copy(ByteBuffer src, ByteBuffer dest) {
        return copy(src, dest, Math.min(src.limit(), dest.remaining()));
    }

    public static ByteBuffer copy(ByteBuffer src, ByteBuffer dest, int length) {
        return copy(src, src.position(), dest, dest.position(), length);
    }

    public static ByteBuffer copy(ByteBuffer src, int srcStart, ByteBuffer dest, int destStart,
                                  int length) {
        System.arraycopy(src.array(), srcStart, dest.array(), destStart, length);
        return dest;
    }

    public static String readUtf8Str(ByteBuffer buffer) {
        return readStr(buffer, CharsetUtil.CHARSET_UTF_8);
    }

    public static String readStr(ByteBuffer buffer, Charset charset) {
        return StrUtil.str(readBytes(buffer), charset);
    }

    public static byte[] readBytes(ByteBuffer buffer) {
        final int remaining = buffer.remaining();
        byte[] ab = new byte[remaining];
        buffer.get(ab);
        return ab;
    }

    public static byte[] readBytes(ByteBuffer buffer, int maxLength) {
        final int remaining = buffer.remaining();
        if (maxLength > remaining) {
            maxLength = remaining;
        }
        byte[] ab = new byte[maxLength];
        buffer.get(ab);
        return ab;
    }

    public static byte[] readBytes(ByteBuffer buffer, int start, int end) {
        byte[] bs = new byte[end - start];
        System.arraycopy(buffer.array(), start, bs, 0, bs.length);
        return bs;
    }

    public static int lineEnd(ByteBuffer buffer) {
        return lineEnd(buffer, buffer.remaining());
    }

    public static int lineEnd(ByteBuffer buffer, int maxLength) {
        int primitivePosition = buffer.position();
        boolean canEnd = false;
        int charIndex = primitivePosition;
        byte b;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            charIndex++;
            if (b == StrUtil.C_CR) {
                canEnd = true;
            } else if (b == StrUtil.C_LF) {
                return canEnd ? charIndex - 2 : charIndex - 1;
            } else {
                canEnd = false;
            }

            if (charIndex - primitivePosition > maxLength) {
                buffer.position(primitivePosition);
                throw new IndexOutOfBoundsException(
                        StrUtil.format("Position is out of maxLength: {}", maxLength));
            }
        }
        buffer.position(primitivePosition);
        return -1;
    }

    public static String readLine(ByteBuffer buffer, Charset charset) {
        final int startPosition = buffer.position();
        final int endPosition = lineEnd(buffer);

        if (endPosition > startPosition) {
            byte[] bs = readBytes(buffer, startPosition, endPosition);
            return StrUtil.str(bs, charset);
        } else if (endPosition == startPosition) {
            return StrUtil.EMPTY;
        }

        return null;
    }

    public static ByteBuffer create(byte[] data) {
        return ByteBuffer.wrap(data);
    }

    public static ByteBuffer create(CharSequence data, Charset charset) {
        return create(StrUtil.bytes(data, charset));
    }

    public static ByteBuffer createUtf8(CharSequence data) {
        return create(StrUtil.utf8Bytes(data));
    }

    public static CharBuffer createCharBuffer(int capacity) {
        return CharBuffer.allocate(capacity);
    }
}
