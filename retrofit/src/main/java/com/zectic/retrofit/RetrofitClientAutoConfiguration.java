package com.zectic.retrofit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetrofitClientAutoConfiguration {

    @Bean
    public HttpClientProxyFactory httpClientProxyFactory() {
        return new HttpClientProxyFactory();
    }
}
