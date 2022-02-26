package com.zectic.retrofit.annotation;

import com.zectic.retrofit.proxy.AbstractInvocationDispatcher;
import com.zectic.retrofit.proxy.DefaultStubProxyFactory;
import com.zectic.retrofit.proxy.StubProxyFactory;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Documented
public @interface ProxyStub {

    /**
     * @return
     */
    @AliasFor(annotation = Component.class, attribute = "value")
    String beanName() default "";

    /**
     */
    Class<? extends StubProxyFactory> factoryType() default DefaultStubProxyFactory.class;

    /**
     */
    @AliasFor("dispatcherType")
    Class<? extends AbstractInvocationDispatcher> value() default AbstractInvocationDispatcher.class;

    /**
     */
    @AliasFor("value")
    Class<? extends AbstractInvocationDispatcher> dispatcherType() default AbstractInvocationDispatcher.class;
}
