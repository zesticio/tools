package com.zestic.core.convert.impl;

import com.zestic.core.convert.AbstractConverter;
import com.zestic.core.convert.Convert;

import java.util.concurrent.atomic.AtomicIntegerArray;

/*
 * {@link AtomicIntegerArray}转换器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.4.5
 */
public class AtomicIntegerArrayConverter extends AbstractConverter<AtomicIntegerArray> {
    private static final long serialVersionUID = 1L;

    @Override protected AtomicIntegerArray convertInternal(Object value) {
        return new AtomicIntegerArray(Convert.convert(int[].class, value));
    }

}
