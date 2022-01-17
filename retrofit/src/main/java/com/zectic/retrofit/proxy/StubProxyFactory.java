package com.zectic.retrofit.proxy;

import com.zectic.retrofit.annotation.ProxyStub;

public interface StubProxyFactory {

    default Class<?>[] collectProxyInterface(Class<?> interfaceType) {
        return new Class[]{interfaceType, StubProxyLabel.class};
    }

    <T> T createProxy(Class<T> stubInterface, ProxyStub stubAnnotation);
}
