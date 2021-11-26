package com.zestic.core.convert.impl;

import com.zestic.core.convert.AbstractConverter;
import com.zestic.core.convert.ConvertException;

/*
 * 强转转换器
 *
 * @param <T> 强制转换到的类型
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 4.0.2
 */
public class CastConverter<T> extends AbstractConverter<T> {
    private static final long serialVersionUID = 1L;

    private Class<T> targetType;

    @Override protected T convertInternal(Object value) {
        // 由于在AbstractConverter中已经有类型判断并强制转换，因此当在上一步强制转换失败时直接抛出异常
        throw new ConvertException("Can not cast value to [{}]", this.targetType);
    }

    @Override public Class<T> getTargetType() {
        return this.targetType;
    }
}
