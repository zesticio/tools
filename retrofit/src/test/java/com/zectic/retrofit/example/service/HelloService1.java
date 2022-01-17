package com.zectic.retrofit.example.service;

import com.zectic.retrofit.example.annotation.TestClient;

@TestClient("helloService")
public interface HelloService1 {
    String hello();
}
