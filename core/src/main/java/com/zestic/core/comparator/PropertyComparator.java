package com.zestic.core.comparator;

import com.zestic.core.bean.BeanUtil;

/*
 * Bean属性排序器<br>
 * 支持读取Bean多层次下的属性
 *
 * @param <T> 被比较的Bean
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
public class PropertyComparator<T> extends FuncComparator<T> {
    private static final long serialVersionUID = 9157326766723846313L;

    /*
     * 构造
     *
     * @param property 属性名
     */
    public PropertyComparator(String property) {
        this(property, true);
    }

    /*
     * 构造
     *
     * @param property      属性名
     * @param isNullGreater null值是否排在后（从小到大排序）
     */
    public PropertyComparator(String property, boolean isNullGreater) {
        super(isNullGreater, (bean) -> BeanUtil.getProperty(bean, property));
    }
}
