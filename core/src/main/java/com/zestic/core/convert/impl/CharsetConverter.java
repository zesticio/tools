package com.zestic.core.convert.impl;

import com.zestic.core.convert.AbstractConverter;
import com.zestic.core.util.CharsetUtil;

import java.nio.charset.Charset;

/*
 * 编码对象转换器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
public class CharsetConverter extends AbstractConverter<Charset> {
    private static final long serialVersionUID = 1L;

    @Override protected Charset convertInternal(Object value) {
        return CharsetUtil.charset(convertToStr(value));
    }

}
