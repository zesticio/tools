package com.zectic.retrofit.example.service;

import com.zectic.retrofit.annotation.ProxyStub;
import com.zectic.retrofit.proxy.AbstractInvocationDispatcher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class InvocationDispatcherImpl2 extends AbstractInvocationDispatcher<ProxyStub, Void> {
    @Override
    protected Object invoke(StubProxyContext<ProxyStub> stubProxyContext, Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("InvocationDispatcherImpl2");
        return "InvocationDispatcherImpl2";
    }
}
