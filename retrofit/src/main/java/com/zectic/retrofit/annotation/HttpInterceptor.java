package com.zectic.retrofit.annotation;

import org.springframework.context.annotation.Import;

import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface HttpInterceptor {

    /**
     * Default message.
     *
     * @return message
     */
    String message() default "Preparation container is not valid";

    /**
     * @return
     */
    Class<?>[] groups() default {};

    /**
     * @return
     */
    Class<? extends Payload>[] payload() default {};
}
