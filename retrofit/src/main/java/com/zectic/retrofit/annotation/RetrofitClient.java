package com.zectic.retrofit.annotation;

import com.zectic.retrofit.config.RetrofitAutoConfiguration;
import org.springframework.context.annotation.Import;

import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@HttpInterceptor
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RetrofitAutoConfiguration.class})
public @interface RetrofitClient {

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
