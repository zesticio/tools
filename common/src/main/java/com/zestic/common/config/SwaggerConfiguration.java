package com.zestic.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
@ConfigurationProperties(prefix = "zestic.app")
public class SwaggerConfiguration {

    private String title = "";
    private String description = "";
    private String termsOfServiceUrl = "";
    private Contact contact;
    private String license = "";
    private String licenseUrl = "";
    private String version = "1.0.0";

    @Bean
    public Docket swaggerConfiguration() {
        // The Docker object to customize API documentation
        return new Docket(DocumentationType.SWAGGER_2).select()
                .apis(RequestHandlerSelectors.basePackage("com.ceva")).build().apiInfo(apiDoc());
    }

    @Bean
    public LinkDiscoverers discoverers() {
        List<LinkDiscoverer> plugins = new ArrayList<>();
        plugins.add(new CollectionJsonLinkDiscoverer());
        return new LinkDiscoverers(SimplePluginRegistry.create(plugins));
    }

//    @Bean
//    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter() {
//        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(new RequestResponseLoggingFilter());
//        registrationBean.addUrlPatterns("/1.0.0/*");
//        return registrationBean;
//    }

    private ApiInfo apiDoc() {
        // Return the Api Info
        return new ApiInfo(title,
                description,
                version,
                termsOfServiceUrl,
                contact,
                license,
                licenseUrl,
                Collections.emptyList());
    }
}
