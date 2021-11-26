package com.zestic.core.annotation;

import com.zestic.core.exceptions.UtilException;
import com.zestic.core.util.ArrayUtil;
import com.zestic.core.util.ReflectUtil;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/*
 * @author <a href="https://www.zestic.io">Deebendu Kumar</a>
 */
public class AnnotationUtil {

    public static CombinationAnnotationElement toCombination(AnnotatedElement annotationEle) {
        if (annotationEle instanceof CombinationAnnotationElement) {
            return (CombinationAnnotationElement) annotationEle;
        }
        return new CombinationAnnotationElement(annotationEle);
    }

    public static Annotation[] getAnnotations(AnnotatedElement annotationEle,
                                              boolean isToCombination) {
        return (null == annotationEle) ?
                null :
                (isToCombination ? toCombination(annotationEle) : annotationEle).getAnnotations();
    }

    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotationEle,
                                                         Class<A> annotationType) {
        return (null == annotationEle) ?
                null :
                toCombination(annotationEle).getAnnotation(annotationType);
    }

    public static boolean hasAnnotation(AnnotatedElement annotationEle,
                                        Class<? extends Annotation> annotationType) {
        return null != getAnnotation(annotationEle, annotationType);
    }

    public static <T> T getAnnotationValue(AnnotatedElement annotationEle,
                                           Class<? extends Annotation> annotationType) throws UtilException {
        return getAnnotationValue(annotationEle, annotationType, "value");
    }

    public static <T> T getAnnotationValue(AnnotatedElement annotationEle,
                                           Class<? extends Annotation> annotationType, String propertyName) throws UtilException {
        final Annotation annotation = getAnnotation(annotationEle, annotationType);
        if (null == annotation) {
            return null;
        }

        final Method method = ReflectUtil.getMethodOfObj(annotation, propertyName);
        if (null == method) {
            return null;
        }
        return ReflectUtil.invoke(annotation, method);
    }

    public static Map<String, Object> getAnnotationValueMap(AnnotatedElement annotationEle,
                                                            Class<? extends Annotation> annotationType) throws UtilException {
        final Annotation annotation = getAnnotation(annotationEle, annotationType);
        if (null == annotation) {
            return null;
        }

        final Method[] methods = ReflectUtil.getMethods(annotationType, t -> {
            if (ArrayUtil.isEmpty(t.getParameterTypes())) {
                // 只读取无参方法
                final String name = t.getName();
                // 跳过自有的几个方法
                return (false == "hashCode".equals(name)) //
                        && (false == "toString".equals(name)) //
                        && (false == "annotationType".equals(name));
            }
            return false;
        });

        final HashMap<String, Object> result = new HashMap<>(methods.length, 1);
        for (Method method : methods) {
            result.put(method.getName(), ReflectUtil.invoke(annotation, method));
        }
        return result;
    }

    public static RetentionPolicy getRetentionPolicy(Class<? extends Annotation> annotationType) {
        final Retention retention = annotationType.getAnnotation(Retention.class);
        if (null == retention) {
            return RetentionPolicy.CLASS;
        }
        return retention.value();
    }

    public static ElementType[] getTargetType(Class<? extends Annotation> annotationType) {
        final Target target = annotationType.getAnnotation(Target.class);
        if (null == target) {
            return new ElementType[]{ElementType.TYPE, //
                    ElementType.FIELD, //
                    ElementType.METHOD, //
                    ElementType.PARAMETER, //
                    ElementType.CONSTRUCTOR, //
                    ElementType.LOCAL_VARIABLE, //
                    ElementType.ANNOTATION_TYPE, //
                    ElementType.PACKAGE//
            };
        }
        return target.value();
    }

    public static boolean isDocumented(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Documented.class);
    }

    public static boolean isInherited(Class<? extends Annotation> annotationType) {
        return annotationType.isAnnotationPresent(Inherited.class);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setValue(Annotation annotation,
                                String annotationField, Object value) {
        final Map memberValues =
                (Map) ReflectUtil.getFieldValue(Proxy.getInvocationHandler(annotation), "memberValues");
        memberValues.put(annotationField, value);
    }
}
