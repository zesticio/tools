package com.zestic.core.convert.impl;

import com.zestic.core.convert.AbstractConverter;

import java.util.TimeZone;

/*
 * TimeZone转换器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
public class TimeZoneConverter extends AbstractConverter<TimeZone> {
    private static final long serialVersionUID = 1L;

    @Override protected TimeZone convertInternal(Object value) {
        return TimeZone.getTimeZone(convertToStr(value));
    }

}
