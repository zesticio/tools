package com.zestic.coin.bouncycastle.util;

public interface Selector
        extends Cloneable {

    boolean match(Object obj);

    Object clone();
}
