package com.zectic.retrofit.example.service;

import com.zectic.retrofit.annotation.ProxyStub;

@ProxyStub(InvocationDispatcherImpl2.class)
public interface HelloService2 {
    String hello();
}
