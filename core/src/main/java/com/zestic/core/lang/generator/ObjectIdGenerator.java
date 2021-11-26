package com.zestic.core.lang.generator;

import com.zestic.core.lang.ObjectId;

/*
 * ObjectId生成器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.4.3
 */
public class ObjectIdGenerator implements Generator<String> {
    @Override public String next() {
        return ObjectId.next();
    }
}
