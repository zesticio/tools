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

package com.zestic.common.exception;

public class NotEnoughDataInByteBufferException extends Exception {

    private int available;
    private int expected;

    public NotEnoughDataInByteBufferException(int p_available, int p_expected) {
        super("Not enough data in byte buffer. " + "Expected " + p_expected
                + ", available: " + p_available + ".");
        available = p_available;
        expected = p_expected;
    }

    public NotEnoughDataInByteBufferException(String s) {
        super(s);
        available = 0;
        expected = 0;
    }

    public int getAvailable() {
        return available;
    }

    public int getExpected() {
        return expected;
    }
}
