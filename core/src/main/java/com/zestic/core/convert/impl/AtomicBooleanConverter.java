package com.zestic.core.convert.impl;

import com.zestic.core.convert.AbstractConverter;
import com.zestic.core.util.BooleanUtil;

import java.util.concurrent.atomic.AtomicBoolean;

/*
 * {@link AtomicBoolean}转换器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 3.0.8
 */
public class AtomicBooleanConverter extends AbstractConverter<AtomicBoolean> {
    private static final long serialVersionUID = 1L;

    @Override protected AtomicBoolean convertInternal(Object value) {
        if (value instanceof Boolean) {
            return new AtomicBoolean((Boolean) value);
        }
        final String valueStr = convertToStr(value);
        return new AtomicBoolean(BooleanUtil.toBoolean(valueStr));
    }

}
