package com.zectic.retrofit.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestTimeout {
    /**
     * Connection timed out
     * -1=default
     * unit second
     */
    int connectTimeout() default -1;

    /**
     * read timeout
     * @return
     */
    int readTimeout() default -1;

    /**
     * @return
     */
    int writeTimeout() default -1;
}
