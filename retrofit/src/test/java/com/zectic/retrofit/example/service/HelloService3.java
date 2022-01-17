package com.zectic.retrofit.example.service;

import com.zectic.retrofit.annotation.ProxyStub;

@ProxyStub(factoryType = StubProxyFactory3.class)
public interface HelloService3 {
    String hello();
}
