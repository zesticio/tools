package com.zestic.core.collection;

import java.util.Iterator;

/*
 * 提供合成接口，共同提供{@link Iterable}和{@link Iterator}功能
 *
 * @param <T> 节点类型
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.7.14
 */
public interface IterableIter<T> extends Iterable<T>, Iterator<T> {
    @Override default Iterator<T> iterator() {
        return this;
    }
}
