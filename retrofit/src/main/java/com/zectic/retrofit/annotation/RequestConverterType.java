package com.zectic.retrofit.annotation;

import com.zectic.retrofit.converter.DefaultRequestBodyConverter;
import com.zectic.retrofit.converter.RequestBodyConverter;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestConverterType {
    Class<? extends RequestBodyConverter> value() default DefaultRequestBodyConverter.class;
}
