package com.zectic.retrofit.config;

import com.zectic.retrofit.bean.RetrofitBean;
import com.zestic.log.Log;
import com.zestic.log.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RetrofitProperties.class)
public class RetrofitAutoConfiguration implements ApplicationContextAware {

    private Log logger = LogFactory.get();
    private ApplicationContext applicationContext;
    private RetrofitProperties properties;

    public RetrofitAutoConfiguration(RetrofitProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * This will execute only when RetrofitBean is not created
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public RetrofitBean createRetrofitBean() {
        RetrofitBean bean = new RetrofitBean(properties);
        return bean;
    }
}
