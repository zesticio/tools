package com.zestic.common.context;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author deebendukumar
 */
public class ApplicationContextImpl extends ConcurrentHashMap<String, Object> implements ApplicationContext {
    private static final long serialVersionUID = -5865286831705661141L;

    private static ApplicationContextImpl _instance;

    private ApplicationContextImpl() {
    }

    public static ApplicationContextImpl getInstance() {
        if (_instance == null) {
            _instance = new ApplicationContextImpl();
        }
        return _instance;
    }

    @Override
    public Object register(String identity, Object interceptor) {
        return put(identity, interceptor);
    }

    @Override
    public Boolean unregister(String identity) {
        return remove(identity) != null;
    }

    @Override
    public Optional<Object> getObject(String identity) {
        return Optional.ofNullable(getObject(identity));
    }

    @Override
    public Boolean hasKey(String identity) {
        return containsKey(identity);
    }

    @Override
    public Boolean empty() {
        clear();
        return true;
    }
}
