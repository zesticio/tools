package com.zectic.retrofit.annotation;

import com.zectic.retrofit.StubBeanDefinitionRegistrar;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * A stubs is a client side (consumer) proxy object which is responsible for communicating
 * with the provider web service. When client calls the web services, it internally first
 * calls stubs, stubs then call the actual service.
 */
@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import(StubBeanDefinitionRegistrar.class)
public @interface ProxyStubScanner {

    @AliasFor("packages")
    String[] value() default {};

    @AliasFor("value")
    String[] packages() default {};

    Class<?>[] classes() default {};

    /**
     * Marker Annotation Interface for qualifying scans
     */
    Class<? extends Annotation> markAnnotation() default Annotation.class;
}
