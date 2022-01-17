package com.zectic.retrofit;

import com.zectic.retrofit.proxy.DefaultStubProxyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StubBeanAutoConfiguration {
    @Bean
    public StubDefPostProcessor stubBeanFactoryPostProcessor() {
        return new StubDefPostProcessor();
    }

    @Bean
    public StubBeanPostProcessor stubBeanPostProcessor() {
        return new StubBeanPostProcessor();
    }

    @Bean
    public DefaultStubProxyFactory defaultStubProxyFactory() {
        return new DefaultStubProxyFactory();
    }
}
