/*
 * Copyright (C) 2012 FuseSource, Inc.
 * http://fusesource.com
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
package com.zestic.dispatch.util;

/*
 * <p>
 * </p>
 *
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public class BufferPool extends ThreadLocalPool<byte[]> {

    private final int bufferSize;

    public BufferPool(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    protected byte[] create() {
        return new byte[bufferSize];
    }

    public int getBufferSize() {
        return bufferSize;
    }

}
