package com.zectic.retrofit.annotation;

import okhttp3.Interceptor;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(HttpInterceptors.class)
public @interface HttpInterceptor {

    Class<? extends Interceptor> value() default Interceptor.class;

    /**
     * Arrange the interceptor chain in ascending order from small to large
     */
    int index() default -1;
}
