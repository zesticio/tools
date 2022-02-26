package com.zectic.retrofit.proxy;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 */
public abstract class AbstractInvocationDispatcher<ANNOTATION_TYPE extends Annotation, ATTACHMENT> {

    private volatile Map<Method, ATTACHMENT> methodAttachments;

    protected final ATTACHMENT getAttachmentOrNull(@NonNull Method key) {
        return getAttachment(key).orElse(null);
    }

    protected final ATTACHMENT getAttachmentOrErr(@NonNull Method key) {
        return getAttachment(key).orElseThrow(NullPointerException::new);
    }

    protected final Optional<ATTACHMENT> getAttachment(@NonNull Method key) {
        return Optional.ofNullable(getMethodAttachments().get(key));
    }

    protected final ATTACHMENT getAttachmentOrCompute(@NonNull Method key, @NonNull Function<Method, ATTACHMENT> function) {
        return getMethodAttachments().computeIfAbsent(key, function);
    }

    private Map<Method, ATTACHMENT> getMethodAttachments() {
        if (methodAttachments == null) {
            synchronized (this) {
                if (methodAttachments == null) {
                    methodAttachments = new ConcurrentHashMap<>();
                }
            }
        }
        return methodAttachments;
    }

    @SuppressWarnings("unchecked")
    Class<ANNOTATION_TYPE> getAnnotationType() {
        return (Class<ANNOTATION_TYPE>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Object invoke(StubProxyContext<ANNOTATION_TYPE> stubProxyContext, Object proxy, Method method, Object[] args) throws Throwable {
        return invoke(proxy, method, args);
    }

    protected Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new UnsupportedOperationException();
    }

    @Getter
    @EqualsAndHashCode
    @ToString
    protected static class StubProxyContext<T extends Annotation> {
        private final T annotation;
        private final Class<?> stubType;

        private StubProxyContext(@NonNull Class<?> stubType, T annotation) {
            this.annotation = annotation;
            this.stubType = stubType;
        }

        public static <T extends Annotation> StubProxyContext<T> valueOf(Class<?> stubType, T annotation) {
            return new StubProxyContext<>(stubType, annotation);
        }
    }
}

