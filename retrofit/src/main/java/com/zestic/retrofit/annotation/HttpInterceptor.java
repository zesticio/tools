package com.zestic.retrofit.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author deebendukumar
 */
@Component
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
//@Repeatable(value = HttpInterceptors.class)
public @interface HttpInterceptor {

    /**
     * Defines the name of the service bean when registered to the underlying context. If left unspecified
     * the name of the service bean is generated using {@link org.springframework.beans.factory.annotation.Qualifier},
     * If no Qualifier annotation, we would use full class name instead.
     *
     * @return the name of the bean.
     */
    String name() default "";

    /**
     * Alias for the {@link #value()} attribute. Allows for more concise annotation
     * declarations e.g.: {@code @RetrofitService("ai")} instead of
     * {@code @RetrofitService(retrofit="ai")}.
     *
     * @return the specified retrofit instance to build endpoint
     */
    String value() default "";

}
