package com.zestic.core.bean.copier.provider;

import com.zestic.core.bean.copier.ValueProvider;
import com.zestic.core.convert.Convert;
import com.zestic.core.map.CaseInsensitiveMap;
import com.zestic.core.util.StrUtil;

import java.lang.reflect.Type;
import java.util.Map;

public class MapValueProvider implements ValueProvider<String> {

    private final Map<?, ?> map;
    private final boolean ignoreError;

    public MapValueProvider(Map<?, ?> map, boolean ignoreCase) {
        this(map, ignoreCase, false);
    }

    public MapValueProvider(Map<?, ?> map, boolean ignoreCase, boolean ignoreError) {
        if (false == ignoreCase || map instanceof CaseInsensitiveMap) {
            this.map = map;
        } else {
            this.map = new CaseInsensitiveMap<>(map);
        }
        this.ignoreError = ignoreError;
    }

    @Override
    public Object value(String key, Type valueType) {
        final String key1 = getKey(key, valueType);
        if (null == key1) {
            return null;
        }

        return Convert.convertWithCheck(valueType, map.get(key1), null, this.ignoreError);
    }

    @Override
    public boolean containsKey(String key) {
        return null != getKey(key, null);
    }

    private String getKey(String key, Type valueType) {
        if (map.containsKey(key)) {
            return key;
        }

        String customKey = StrUtil.toUnderlineCase(key);
        if (map.containsKey(customKey)) {
            return customKey;
        }

        if (null == valueType || Boolean.class == valueType || boolean.class == valueType) {
            customKey = StrUtil.upperFirstAndAddPre(key, "is");
            if (map.containsKey(customKey)) {
                return customKey;
            }
            customKey = StrUtil.toUnderlineCase(customKey);
            if (map.containsKey(customKey)) {
                return customKey;
            }
        }
        return null;
    }
}
