package com.zectic.retrofit.example.annotation;

import com.zectic.retrofit.annotation.ProxyStub;
import com.zectic.retrofit.example.service.InvocationDispatcherImpl1;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ProxyStub(InvocationDispatcherImpl1.class)
public @interface TestClient {
    @AliasFor(annotation = ProxyStub.class, attribute = "beanName")
    String value() default "";
}
