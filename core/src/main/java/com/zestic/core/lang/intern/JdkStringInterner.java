package com.zestic.core.lang.intern;

/*
 * JDK中默认的字符串规范化实现
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.4.3
 */
public class JdkStringInterner implements Interner<String> {
    @Override public String intern(String sample) {
        if (null == sample) {
            return null;
        }
        return sample.intern();
    }
}
