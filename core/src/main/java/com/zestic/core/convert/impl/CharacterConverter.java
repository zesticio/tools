package com.zestic.core.convert.impl;

import com.zestic.core.convert.AbstractConverter;
import com.zestic.core.util.BooleanUtil;
import com.zestic.core.util.StrUtil;

/*
 * 字符转换器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
public class CharacterConverter extends AbstractConverter<Character> {
    private static final long serialVersionUID = 1L;

    @Override protected Character convertInternal(Object value) {
        if (value instanceof Boolean) {
            return BooleanUtil.toCharacter((Boolean) value);
        } else {
            final String valueStr = convertToStr(value);
            if (StrUtil.isNotBlank(valueStr)) {
                return valueStr.charAt(0);
            }
        }
        return null;
    }

}
