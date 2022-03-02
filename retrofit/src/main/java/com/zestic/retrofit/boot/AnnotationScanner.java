package com.zestic.retrofit.boot;

import com.zestic.common.utils.ClassInspectionUtil;
import com.zestic.retrofit.annotation.HttpInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Collection;

/**
 * Scan for a given annotation using reflection API
 */
/**
 * @author deebendukumar
 */
public class AnnotationScanner {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(RetrofitAutoConfiguration.class);

    static AnnotationScanner getInstance() {
        return new AnnotationScanner();
    }

    private AnnotationScanner() {
    }

    /**
     * using reflection library we can scans the provided class path for all classes at the runtime
     *
     * @param packageName
     * @param annotationClass
     * @return
     */
    public void scan(String packageName, Class<? extends Annotation> annotationClass) {
        Collection<Class<?>> classes = ClassInspectionUtil.findAnnotatedClasses(HttpInterceptor.class, packageName);
        logger.info(classes);
    }
}
