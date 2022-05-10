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

public class ByteArrayInputStream extends InputStream {

    private byte[] data;
    private int pos;

    public ByteArrayInputStream(byte[] partsData) {
        this.data = partsData;
    }

    public int read() throws IOException {
        if (pos == data.length)
            return -1;
        return data[pos++] & 0xff;
    }

    public int read(byte b[], int s, int l) throws IOException {
        if (l == 0)
            return 0;
        if (pos == data.length)
            return -1;
        if (l + pos > data.length)
            l = data.length - pos;
        System.arraycopy(data, pos, b, s, l);
        pos += l;
        return l;
    }

    /**
     * Returns the count of available bytes, limited by the defined limit.
     */
    public int available() throws IOException {
        final int a = data.length - pos;
        return a;
    }

    public long skip(long n) throws IOException {
        if (n + pos > data.length) {
            long r = data.length - pos;
            pos = data.length;
            return r;
        }

        pos += (int) n;
        return n;
    }
}
