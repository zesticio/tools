package com.zestic.core.text.finder;

import com.zestic.core.lang.Assert;
import com.zestic.core.util.NumberUtil;

/*
 * 字符查找器
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.7.14
 */
public class CharFinder extends TextFinder {
    private static final long serialVersionUID = 1L;

    private final char c;
    private final boolean caseInsensitive;

    /*
     * 构造，不忽略字符大小写
     *
     * @param c 被查找的字符
     */
    public CharFinder(char c) {
        this(c, false);
    }

    /*
     * 构造
     *
     * @param c               被查找的字符
     * @param caseInsensitive 是否忽略大小写
     */
    public CharFinder(char c, boolean caseInsensitive) {
        this.c = c;
        this.caseInsensitive = caseInsensitive;
    }

    @Override public int start(int from) {
        Assert.notNull(this.text, "Text to find must be not null!");
        final int length = text.length();
        for (int i = from; i < length; i++) {
            if (NumberUtil.equals(c, text.charAt(i), caseInsensitive)) {
                return i;
            }
        }
        return -1;
    }

    @Override public int end(int start) {
        if (start < 0) {
            return -1;
        }
        return start + 1;
    }
}
