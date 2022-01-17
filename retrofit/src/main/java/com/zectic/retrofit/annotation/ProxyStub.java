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
     * 指定beanName
     *
     * @return
     */
    @AliasFor(annotation = Component.class, attribute = "value")
    String beanName() default "";

    /**
     * 指定动态代理工厂,用于定制动态代理方案比如Cglib、ByteBuddy等
     * 默认使用基于JdkDynamicProxy代理并使用AbstractInvocationDispatcher拦截方法调用
     */
    Class<? extends StubProxyFactory> factoryType() default DefaultStubProxyFactory.class;

    /**
     * 指定JdkDynamicProxy调用拦截器BeanType,用于从BeanFactory中按类型获取bean
     */
    @AliasFor("dispatcherType")
    Class<? extends AbstractInvocationDispatcher> value() default AbstractInvocationDispatcher.class;

    /**
     * 指定JdkDynamicProxy调用拦截器BeanType,用于从BeanFactory中按类型获取bean
     */
    @AliasFor("value")
    Class<? extends AbstractInvocationDispatcher> dispatcherType() default AbstractInvocationDispatcher.class;
}
