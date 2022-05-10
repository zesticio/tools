/*
 * Version:  1.0.0
 *
 * Authors:  Kumar <Deebendu Kumar>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zestic.common.utils;

import java.io.IOException;
import java.io.OutputStream;

public class BufferedOutputStream extends OutputStream {

    private final int bufferSize;
    private byte[] buffer;
    private int pos;
    private OutputStream os;

    public BufferedOutputStream(OutputStream os, int bufferSize) {
        this.os = os;
        this.bufferSize = bufferSize;
        buffer = new byte[bufferSize];
    }

    public void write(int b) throws IOException {
        if (pos == bufferSize)
            flush();
        buffer[pos++] = (byte) b;
    }

    public void flush() throws IOException {
        if (pos > 0) {
            os.write(buffer, 0, pos);
            os.flush();
            pos = 0;
        }
    }

    public void close() throws IOException {
        flush();
        os.close();
    }

    public void write(byte[] b, int off, int len) throws IOException {
        while (pos + len > bufferSize) {
            if (pos == bufferSize)
                flush();
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

    public String toString() {
        return new String(buffer, 0, 0, pos);
    }
}
