package com.zestic.core.annotation;

import java.lang.annotation.*;

/*
 * Alias annotations, use this annotation fields, methods, parameters, etc. Will have an alias, 
 * used to transfer copy Bean, Bean to the Map, etc
 *
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
@Documented @Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER}) public @interface Alias {

    /*
     * The alias value, that is, use this annotation to replace the alias name
     *
     * @return The alias value
     */
    String value();
}
