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
import java.io.InputStream;

public class BufferedInputStream extends InputStream {

    private InputStream is;
    private int pos;
    private int end;
    private byte[] buffer;

    public BufferedInputStream(InputStream is, int bufferSize) {
        this.is = is;
        bufferSize = bufferSize > 0 ? bufferSize : 4096;
        buffer = new byte[bufferSize];
    }

    public int available() throws IOException {
        if (pos == end && is.available() > 0)
            fill();
        return end - pos;
    }

    private void fill() throws IOException {
        int b0 = is.read();
        if (b0 < 0)
            return;
        buffer[0] = (byte) b0;
        int canRead = buffer.length - 1;
        final int av = is.available();
        if (av < canRead)
            canRead = av;
        if (canRead > 0)
            end = 1 + is.read(buffer, 1, canRead);
        else
            end = 1;
        pos = 0;
    }

    public void close() throws IOException {
        is.close();
        pos = end;
    }

    public int read() throws IOException {
        if (pos == end)
            fill();
        if (pos == end)
            return -1;
        return 0xff & buffer[pos++];
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (pos == end)
            fill();
        int avail = end - pos;
        if (len > avail)
            len = avail;
        System.arraycopy(buffer, pos, b, off, len);
        pos += len;
        return len;
    }

    public void reassign(InputStream is) {
        pos = end = 0;
        this.is = is;
    }
}
