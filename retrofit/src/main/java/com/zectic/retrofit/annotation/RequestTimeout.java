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
     * -1=默认值
     * 单位秒
     *
     * @return
     */
    int readTimeout() default -1;

    /**
     * 写超时
     * -1=默认值
     * 单位秒
     *
     * @return
     */
    int writeTimeout() default -1;
}
