package com.zestic.core.text.finder;

import com.zestic.core.lang.Assert;

/*
 * 固定长度查找器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.7.14
 */
public class LengthFinder extends TextFinder {
    private static final long serialVersionUID = 1L;

    private final int length;

    /*
     * 构造
     *
     * @param length 长度
     */
    public LengthFinder(int length) {
        this.length = length;
    }

    @Override public int start(int from) {
        Assert.notNull(this.text, "Text to find must be not null!");
        final int result = from + length;
        if (result < text.length()) {
            return result;
        }
        return -1;
    }

    @Override public int end(int start) {
        return start;
    }
}
