package com.zestic.core.lang.generator;

import com.zestic.core.util.IdUtil;

/*
 * UUID生成器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.4.3
 */
public class UUIDGenerator implements Generator<String> {
    @Override public String next() {
        return IdUtil.fastUUID();
    }
}
