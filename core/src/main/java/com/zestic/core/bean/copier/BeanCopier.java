package com.zestic.core.bean.copier;

import com.zestic.core.bean.BeanException;
import com.zestic.core.bean.BeanUtil;
import com.zestic.core.bean.DynaBean;
import com.zestic.core.bean.copier.provider.BeanValueProvider;
import com.zestic.core.bean.copier.provider.DynaBeanValueProvider;
import com.zestic.core.bean.copier.provider.MapValueProvider;
import com.zestic.core.collection.CollUtil;
import com.zestic.core.lang.copier.Copier;
import com.zestic.core.util.StrUtil;
import com.zestic.core.util.TypeUtil;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;

public class BeanCopier<T> implements Copier<T>, Serializable {
    private static final long serialVersionUID = 1L;

    private final Object source;

    private final T dest;

    private final Type destType;

    private final CopyOptions copyOptions;

    public BeanCopier(Object source, T dest, Type destType, CopyOptions copyOptions) {
        this.source = source;
        this.dest = dest;
        this.destType = destType;
        this.copyOptions = copyOptions;
    }

    public static <T> BeanCopier<T> create(Object source, T dest, CopyOptions copyOptions) {
        return create(source, dest, dest.getClass(), copyOptions);
    }

    public static <T> BeanCopier<T> create(Object source, T dest, Type destType,
                                           CopyOptions copyOptions) {
        return new BeanCopier<>(source, dest, destType, copyOptions);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T copy() {
        if (null != this.source) {
            if (this.source instanceof ValueProvider) {
                valueProviderToBean((ValueProvider<String>) this.source, this.dest);
            } else if (this.source instanceof DynaBean) {
                valueProviderToBean(
                        new DynaBeanValueProvider((DynaBean) this.source, copyOptions.ignoreError),
                        this.dest);
            } else if (this.source instanceof Map) {
                if (this.dest instanceof Map) {
                    mapToMap((Map<?, ?>) this.source, (Map<?, ?>) this.dest);
                } else {
                    mapToBean((Map<?, ?>) this.source, this.dest);
                }
            } else {
                if (this.dest instanceof Map) {
                    beanToMap(this.source, (Map<?, ?>) this.dest);
                } else {
                    beanToBean(this.source, this.dest);
                }
            }
        }

        return this.dest;
    }

    private void beanToBean(Object providerBean, Object destBean) {
        valueProviderToBean(new BeanValueProvider(providerBean, this.copyOptions.ignoreCase,
                this.copyOptions.ignoreError), destBean);
    }

    private void mapToBean(Map<?, ?> map, Object bean) {
        valueProviderToBean(
                new MapValueProvider(map, this.copyOptions.ignoreCase, this.copyOptions.ignoreError),
                bean);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void mapToMap(Map source, Map dest) {
        if (null != dest && null != source) {
            dest.putAll(source);
        }
    }

    private void beanToMap(Object bean, Map targetMap) {
        final HashSet<String> ignoreSet = (null != copyOptions.ignoreProperties) ?
                CollUtil.newHashSet(copyOptions.ignoreProperties) :
                null;
        final CopyOptions copyOptions = this.copyOptions;

        BeanUtil.descForEach(bean.getClass(), (prop) -> {
            if (false == prop.isReadable(copyOptions.isTransientSupport())) {
                return;
            }
            String key = prop.getFieldName();
            if (CollUtil.contains(ignoreSet, key)) {
                return;
            }

            key = copyOptions.editFieldName(copyOptions.getMappedFieldName(key, false));
            if (null == key) {
                return;
            }

            Object value;
            try {
                value = prop.getValue(bean);
            } catch (Exception e) {
                if (copyOptions.ignoreError) {
                    return;
                } else {
                    throw new BeanException(e, "Get value of [{}] error!", prop.getFieldName());
                }
            }
            if (null != copyOptions.propertiesFilter && false == copyOptions.propertiesFilter.test(
                    prop.getField(), value)) {
                return;
            }
            if ((null == value && copyOptions.ignoreNullValue) || bean == value) {
                return;
            }
            targetMap.put(key, value);
        });
    }

    private void valueProviderToBean(ValueProvider<String> valueProvider, Object bean) {
        if (null == valueProvider) {
            return;
        }

        final CopyOptions copyOptions = this.copyOptions;
        Class<?> actualEditable = bean.getClass();
        if (null != copyOptions.editable) {
            if (false == copyOptions.editable.isInstance(bean)) {
                throw new IllegalArgumentException(
                        StrUtil.format("Target class [{}] not assignable to Editable class [{}]",
                                bean.getClass().getName(), copyOptions.editable.getName()));
            }
            actualEditable = copyOptions.editable;
        }
        final HashSet<String> ignoreSet = (null != copyOptions.ignoreProperties) ?
                CollUtil.newHashSet(copyOptions.ignoreProperties) :
                null;
        BeanUtil.descForEach(actualEditable, (prop) -> {
            if (false == prop.isWritable(this.copyOptions.isTransientSupport())) {
                return;
            }
            String fieldName = prop.getFieldName();
            if (CollUtil.contains(ignoreSet, fieldName)) {
                return;
            }
            final String providerKey =
                    copyOptions.editFieldName(copyOptions.getMappedFieldName(fieldName, true));
            if (null == providerKey) {
                return;
            }
            if (false == valueProvider.containsKey(providerKey)) {
                // 无对应值可提供
                return;
            }

            // 获取目标字段真实类型
            final Type fieldType = TypeUtil.getActualType(this.destType, prop.getFieldType());

            // 获取属性值
            Object value = valueProvider.value(providerKey, fieldType);
            if (null != copyOptions.propertiesFilter && false == copyOptions.propertiesFilter.test(
                    prop.getField(), value)) {
                return;
            }

            if ((null == value && copyOptions.ignoreNullValue) || bean == value) {
                // 当允许跳过空时，跳过
                // 值不能为bean本身，防止循环引用
                return;
            }

            prop.setValue(bean, value, copyOptions.ignoreNullValue, copyOptions.ignoreError);
        });
    }
}
