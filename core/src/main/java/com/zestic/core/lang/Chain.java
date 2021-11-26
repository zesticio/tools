package com.zestic.core.lang;

/*
 * 责任链接口
 *
 * @param <E> 元素类型
 * @param <T> 目标类类型，用于返回this对象
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
public interface Chain<E, T> extends Iterable<E> {
    /*
     * 加入责任链
     *
     * @param element 责任链新的环节元素
     * @return this
     */
    T addChain(E element);
}
