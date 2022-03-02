package com.zestic.common.utils;

import com.google.common.base.Function;
import com.zestic.common.inspection.ClassAnnotationMetadata;
import com.zestic.common.inspection.ClassInspector;
import com.zestic.common.inspection.collect.ClassCollector;
import com.zestic.common.inspection.inspect.AnnotatedElementsAnnotatedWith;
import com.zestic.common.inspection.inspect.ClassesAnnotatedWith;
import com.zestic.common.inspection.inspect.ClassesAssignableFrom;
import com.zestic.common.inspection.inspect.ClassesImplementing;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 * Utilities to inspect and discover classes (elements) that match a specific
 * criteria.
 */
public class ClassInspectionUtil {

    /**
     * Collects elements that are matched by the given inspector.
     *
     * @param inspector
     * @param packageName package name to scan recursively
     * @return matched classes
     */
    public static <T, CI extends ClassInspector<T>> Collection<T> findElements(CI inspector, String packageName) {
        return new ClassCollector<T, CI>(inspector, packageName).findAndLetInspect().getElements();
    }

    /**
     * Collects classes that are assignable from the given class.
     *
     * @param clazz       the class that matching classes should be assignable from
     * @param packageName package name to scan recursively
     * @return matched classes
     */
    public static <T> Collection<Class<? extends T>> findClassesAssignableFrom(Class<T> clazz, String packageName) {
        return findElements(new ClassesAssignableFrom<>(clazz), packageName);
    }

    public static <T> Collection<Class<? extends T>> findClassesImplementing(Class<T> iface, String packageName) {
        if (!iface.isInterface()) {
            throw new IllegalArgumentException("'" + iface + "' is no Interface!");
        }
        return findElements(new ClassesImplementing<>(iface), packageName);
    }

    /**
     * Collects classes that are annotated with the annotation.
     *
     * @param annotation
     * @param packageName package name to scan recursively
     * @return matched classes
     */
    public static Collection<Class<?>> findAnnotatedClasses(Class<? extends Annotation> annotation, String packageName) {
        return findElements(new ClassesAnnotatedWith(annotation), packageName);
    }

    /**
     * Collects class metadata of classes and members are annotated with the
     * annotation.
     *
     * @param annotation
     * @param packageName package name to scan recursively
     * @return matched classes
     */
    public static Collection<ClassAnnotationMetadata> findAnnotatedElements(Class<? extends Annotation> annotation, String packageName) {
        return findElements(new AnnotatedElementsAnnotatedWith(annotation), packageName);
    }

    /**
     * @return function class to name
     */
    public static Function<Class<?>, String> classToName() {
        return ToClassName.INSTANCE;
    }

    private enum ToClassName implements Function<Class<?>, String> {
        INSTANCE;

        @Override
        public String apply(Class<?> input) {
            return input.getName();
        }
    }

}
