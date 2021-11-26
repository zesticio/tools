/*
 * MIT License
 *
 * Copyright (c) 2010 - 2021 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zestic.system.util.tuples;

import com.zestic.system.annotation.concurrent.ThreadSafe;

/*
 * Convenience class for returning multiple objects from methods.
 *
 * @param <A> Type of the first element
 * @param <B> Type of the second element
 * @param <C> Type of the third element
 * @param <D> Type of the fourth element
 * @param <E> Type of the fifth element
 */
@ThreadSafe public class Quintet<A, B, C, D, E> {

    private final A a;
    private final B b;
    private final C c;
    private final D d;
    private final E e;

    /*
     * Create a quintet and store five objects.
     *
     * @param a the first object to store
     * @param b the second object to store
     * @param c the third object to store
     * @param d the fourth object to store
     * @param e the fifth object to store
     */
    public Quintet(A a, B b, C c, D d, E e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    /*
     * Returns the first stored object.
     *
     * @return first object stored
     */
    public final A getA() {
        return a;
    }

    /*
     * Returns the second stored object.
     *
     * @return second object stored
     */
    public final B getB() {
        return b;
    }

    /*
     * Returns the third stored object.
     *
     * @return third object stored
     */
    public final C getC() {
        return c;
    }

    /*
     * Returns the fourth stored object.
     *
     * @return fourth object stored
     */
    public final D getD() {
        return d;
    }

    /*
     * Returns the fifth stored object.
     *
     * @return fifth object stored
     */
    public final E getE() {
        return e;
    }
}