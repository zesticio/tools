package com.zectic.retrofit.annotation;

import com.zectic.retrofit.converter.DefaultReplyBodyConverter;
import com.zectic.retrofit.converter.ReplyBodyConverter;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReplyConverterType {

    Class<? extends ReplyBodyConverter> value() default DefaultReplyBodyConverter.class;
}
