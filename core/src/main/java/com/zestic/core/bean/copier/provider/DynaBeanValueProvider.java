package com.zestic.core.bean.copier.provider;

import com.zestic.core.bean.DynaBean;
import com.zestic.core.bean.copier.ValueProvider;
import com.zestic.core.convert.Convert;

import java.lang.reflect.Type;

/*
 * DynaBean值提供者
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 * @since 5.4.2
 */
public class DynaBeanValueProvider implements ValueProvider<String> {

    private final DynaBean dynaBean;
    private final boolean ignoreError;

    /*
     * 构造
     *
     * @param dynaBean    DynaBean
     * @param ignoreError 是否忽略错误
     */
    public DynaBeanValueProvider(DynaBean dynaBean, boolean ignoreError) {
        this.dynaBean = dynaBean;
        this.ignoreError = ignoreError;
    }

    @Override public Object value(String key, Type valueType) {
        final Object value = dynaBean.get(key);
        return Convert.convertWithCheck(valueType, value, null, this.ignoreError);
    }

    @Override public boolean containsKey(String key) {
        return dynaBean.containsProp(key);
    }

}
