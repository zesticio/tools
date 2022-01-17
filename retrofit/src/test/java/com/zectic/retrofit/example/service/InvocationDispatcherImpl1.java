package com.zectic.retrofit.example.service;

import com.zectic.retrofit.example.annotation.TestClient;
import com.zectic.retrofit.proxy.AbstractInvocationDispatcher;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class InvocationDispatcherImpl1 extends AbstractInvocationDispatcher<TestClient, Void> {
    @Override
    protected Object invoke(StubProxyContext<TestClient> stubProxyContext, Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("InvocationDispatcherImpl1");
        return "InvocationDispatcherImpl1";
    }
}
