package com.zectic.retrofit.proxy;

import com.zectic.retrofit.annotation.ProxyStub;
import lombok.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DefaultStubProxyFactory implements StubProxyFactory, BeanFactoryAware {

    private BeanFactory beanFactory;

    @SuppressWarnings("rawtypes,unchecked")
    @Override
    public <T> T createProxy(Class<T> stubInterface, ProxyStub stubAnnotation) {
        AbstractInvocationDispatcher invocationDispatcher = getInvocationDispatcher(stubInterface, stubAnnotation);
        Class annotationType = invocationDispatcher.getAnnotationType();
        Annotation annotation = AnnotationUtils.getAnnotation(stubInterface, annotationType);
        AbstractInvocationDispatcher.StubProxyContext<?> stubProxyContext = AbstractInvocationDispatcher.StubProxyContext.valueOf(stubInterface, annotation);
        return (T) Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), collectProxyInterface(stubInterface), StubInvocationHandler.newInstance(stubProxyContext, invocationDispatcher));
    }

    @SuppressWarnings("rawtypes")
    private AbstractInvocationDispatcher getInvocationDispatcher(@NonNull Class<?> type, @NonNull ProxyStub proxyStub) {
        Class<? extends AbstractInvocationDispatcher> dispatcherType = proxyStub.dispatcherType();
        Object handler;
        if (dispatcherType != AbstractInvocationDispatcher.class) {
            handler = beanFactory.getBean(dispatcherType);
        } else {
            throw new BeanCreationException(type.getName() + " 没有指定InvocationDispatcher");
        }
        return (AbstractInvocationDispatcher) handler;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @SuppressWarnings("rawtypes")
    static class StubInvocationHandler implements InvocationHandler {

        private final AbstractInvocationDispatcher.StubProxyContext stubProxyContext;
        private final AbstractInvocationDispatcher dispatcher;

        private StubInvocationHandler(AbstractInvocationDispatcher.StubProxyContext stubProxyContext, AbstractInvocationDispatcher dispatcher) {
            this.stubProxyContext = stubProxyContext;
            this.dispatcher = dispatcher;
        }

        public static StubInvocationHandler newInstance(@NonNull AbstractInvocationDispatcher.StubProxyContext stubProxyContext, @NonNull AbstractInvocationDispatcher dispatcher) {
            return new StubInvocationHandler(stubProxyContext, dispatcher);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isToStringMethod(method)) {
                return "ProxyStub:" + ClassUtils.classNamesToString(stubProxyContext.getStubType()) + ":" + stubProxyContext.getAnnotation();
            }
            if (ReflectionUtils.isEqualsMethod(method)
                    || ReflectionUtils.isHashCodeMethod(method)) {
                return method.invoke(this, args);
            }
            return dispatcher.invoke(stubProxyContext, proxy, method, args);
        }
    }
}
