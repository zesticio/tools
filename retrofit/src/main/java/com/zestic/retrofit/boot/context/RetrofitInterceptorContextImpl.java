package com.zestic.retrofit.boot.context;

import okhttp3.Interceptor;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author deebendukumar
 */
public class RetrofitInterceptorContextImpl extends ConcurrentHashMap<String, Interceptor> implements RetrofitInterceptorContext {
    private static final long serialVersionUID = -5865286831705661141L;

    private static RetrofitInterceptorContextImpl _instance;

    private RetrofitInterceptorContextImpl() {
    }

    public static RetrofitInterceptorContextImpl getInstance() {
        if (_instance == null) {
            _instance = new RetrofitInterceptorContextImpl();
        }
        return _instance;
    }

    @Override
    public Interceptor register(String identity, Interceptor interceptor) {
        return put(identity, interceptor);
    }

    @Override
    public boolean unregister(String identity) {
        return remove(identity) != null;
    }

    @Override
    public Optional<Interceptor> getRetrofit(String identity) {
        return Optional.ofNullable(get(identity));
    }

    @Override
    public boolean hasRetrofit(String identity) {
        return containsKey(identity);
    }

    @Override
    public boolean empty() {
        clear();
        return true;
    }
}
