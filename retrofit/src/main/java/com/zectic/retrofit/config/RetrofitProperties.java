package com.zectic.retrofit.config;

import lombok.Data;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "retrofit")
public class RetrofitProperties implements Serializable {

    private String baseUri = "";
    private Integer connectTimeout = 60;
    private Integer readTimeout = 60;
    private Integer writeTimeout = 60;

    private Boolean logging = Boolean.TRUE;
    private HttpLoggingInterceptor.Level logLevel = HttpLoggingInterceptor.Level.BODY;
}
