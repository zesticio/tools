package com.zestic.core.annotation;

import com.zestic.core.collection.CollUtil;

import java.io.Serializable;
import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

public class CombinationAnnotationElement implements AnnotatedElement, Serializable {
    private static final long serialVersionUID = 1L;

    private static final Set<Class<? extends Annotation>> META_ANNOTATIONS =
            CollUtil.newHashSet(Target.class, //
                    Retention.class, //
                    Inherited.class, //
                    Documented.class, //
                    SuppressWarnings.class, //
                    Override.class, //
                    Deprecated.class//
            );

    private Map<Class<? extends Annotation>, Annotation> annotationMap;
    private Map<Class<? extends Annotation>, Annotation> declaredAnnotationMap;

    public CombinationAnnotationElement(AnnotatedElement element) {
        init(element);
    }

    @Override
    public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
        return annotationMap.containsKey(annotationClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        Annotation annotation = annotationMap.get(annotationClass);
        return (annotation == null) ? null : (T) annotation;
    }

    @Override
    public Annotation[] getAnnotations() {
        final Collection<Annotation> annotations = this.annotationMap.values();
        return annotations.toArray(new Annotation[0]);
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        final Collection<Annotation> annotations = this.declaredAnnotationMap.values();
        return annotations.toArray(new Annotation[0]);
    }

    private void init(AnnotatedElement element) {
        final Annotation[] declaredAnnotations = element.getDeclaredAnnotations();
        this.declaredAnnotationMap = new HashMap<>();
        parseDeclared(declaredAnnotations);

        final Annotation[] annotations = element.getAnnotations();
        if (Arrays.equals(declaredAnnotations, annotations)) {
            this.annotationMap = this.declaredAnnotationMap;
        } else {
            this.annotationMap = new HashMap<>();
            parse(annotations);
        }
    }

    private void parseDeclared(Annotation[] annotations) {
        Class<? extends Annotation> annotationType;
        for (Annotation annotation : annotations) {
            annotationType = annotation.annotationType();
            if (false == META_ANNOTATIONS.contains(annotationType)) {
                declaredAnnotationMap.put(annotationType, annotation);
                parseDeclared(annotationType.getDeclaredAnnotations());
            }
        }
    }

    private void parse(Annotation[] annotations) {
        Class<? extends Annotation> annotationType;
        for (Annotation annotation : annotations) {
            annotationType = annotation.annotationType();
            if (false == META_ANNOTATIONS.contains(annotationType)) {
                annotationMap.put(annotationType, annotation);
                parse(annotationType.getAnnotations());
            }
        }
    }
}
