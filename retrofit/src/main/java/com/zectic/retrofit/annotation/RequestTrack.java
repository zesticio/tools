package com.zectic.retrofit.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestTrack {
    String topic() default "";

    String[] tags() default {};
}
