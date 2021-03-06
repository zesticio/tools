package com.zestic.core.lang.intern;

import com.zestic.core.lang.SimpleCache;

/*
 * 使用WeakHashMap(线程安全)存储对象的规范化对象，注意此对象需单例使用！<br>
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.4.3
 */
public class WeakInterner<T> implements Interner<T> {

    private final SimpleCache<T, T> cache = new SimpleCache<>();

    @Override public T intern(T sample) {
        if (null == sample) {
            return null;
        }
        return cache.get(sample, () -> sample);
    }
}
