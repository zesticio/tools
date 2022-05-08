package com.zestic.common.context;

import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author deebendukumar
 */
public class ApplicationImpl extends ConcurrentHashMap<String, Object> implements Application {
    private static final long serialVersionUID = -5865286831705661141L;

    private static ApplicationImpl _instance;
    private ApplicationContext context;


    private ApplicationImpl() {
    }

    public static ApplicationImpl getInstance() {
        if (_instance == null) {
            _instance = new ApplicationImpl();
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

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }
}
