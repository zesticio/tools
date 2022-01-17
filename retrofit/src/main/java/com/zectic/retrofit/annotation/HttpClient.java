package com.zectic.retrofit.annotation;

import com.zectic.retrofit.HttpClientProxyFactory;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ProxyStub(factoryType = HttpClientProxyFactory.class)
public @interface HttpClient {

    @AliasFor("value")
    String contextPath() default "";

    @AliasFor("contextPath")
    String value() default "";
}
