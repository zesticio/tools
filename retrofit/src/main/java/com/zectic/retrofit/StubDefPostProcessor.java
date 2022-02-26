package com.zectic.retrofit;

import com.zectic.retrofit.support.BeanDefinitionRegistryPostProcessorAdapter;
import com.zectic.retrofit.support.ClassPathStubBeanDefinitionScanner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class StubDefPostProcessor extends BeanDefinitionRegistryPostProcessorAdapter implements EnvironmentAware, BeanFactoryAware {

    private Environment environment;
    private BeanFactory beanFactory;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        List<String> pkgList = AutoConfigurationPackages.get(beanFactory);
        if (CollectionUtils.isEmpty(pkgList)) {
            log.debug("The package path to scan is empty...skip...");
            return;
        }
        ClassPathBeanDefinitionScanner scanner = new ClassPathStubBeanDefinitionScanner(registry, environment);
        int bdCnt = scanner.scan(StringUtils.toStringArray(pkgList));
        log.debug("Scanned {} SpringStubs from {} in this round", pkgList, bdCnt);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
