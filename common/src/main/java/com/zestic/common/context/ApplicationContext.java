package com.zestic.common.context;

import java.util.Optional;

/**
 * @author deebendukumar
 */
public interface ApplicationContext {

    Object register(String key, Object value);

    Boolean unregister(String key);

    Optional<Object> getObject(String key);

    Object hasKey(String key);

    Object empty();
}
