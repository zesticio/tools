package com.zectic.retrofit.example;

import com.zectic.retrofit.annotation.ProxyStubScanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@ProxyStubScanner("com.github.yungyu16.spring.stub.processor")
public class TestApplication {
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }
}
