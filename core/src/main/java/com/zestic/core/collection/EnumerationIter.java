package com.zestic.core.collection;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Iterator;

/*
 * {@link Enumeration}对象转{@link Iterator}对象
 *
 * @param <E> 元素类型
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 4.1.1
 */
public class EnumerationIter<E> implements IterableIter<E>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Enumeration<E> e;

    /*
     * 构造
     *
     * @param enumeration {@link Enumeration}对象
     */
    public EnumerationIter(Enumeration<E> enumeration) {
        this.e = enumeration;
    }

    @Override public boolean hasNext() {
        return e.hasMoreElements();
    }

    @Override public E next() {
        return e.nextElement();
    }

    @Override public void remove() {
        throw new UnsupportedOperationException();
    }
}
