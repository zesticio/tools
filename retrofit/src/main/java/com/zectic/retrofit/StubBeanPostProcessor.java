package com.zectic.retrofit;

import com.zectic.retrofit.annotation.ProxyStub;
import com.zectic.retrofit.proxy.StubProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.annotation.AnnotationUtils;

public class StubBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements BeanFactoryAware {
    private BeanFactory beanFactory;

    @Override
    @SuppressWarnings("all")
    public Object postProcessBeforeInstantiation(Class<?> type, String name) throws BeansException {
        ProxyStub proxyStub = AnnotationUtils.getAnnotation(type, ProxyStub.class);
        if (proxyStub == null) {
            return null;
        }
        if (!type.isInterface()) {
            throw new BeanCreationNotAllowedException(name, type.getName() + " 不是Interface");
        }
        StubProxyFactory stubProxyFactory = getStubProxyFactory(proxyStub);
        return stubProxyFactory.createProxy(type, proxyStub);
    }

    private StubProxyFactory getStubProxyFactory(ProxyStub proxyStub) {
        Class<? extends StubProxyFactory> factoryType = proxyStub.factoryType();
        return beanFactory.getBean(factoryType);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
