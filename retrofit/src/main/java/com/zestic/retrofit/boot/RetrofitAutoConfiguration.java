package com.zestic.retrofit.boot;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zestic.retrofit.boot.context.RetrofitInterceptorContextImpl;
import com.zestic.retrofit.config.Connection;
import com.zestic.retrofit.config.Log;
import com.zestic.retrofit.config.RetrofitProperties;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author deebendukumar
 */
@Configuration
@EnableConfigurationProperties(RetrofitProperties.class)
public class RetrofitAutoConfiguration implements ApplicationContextAware {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(RetrofitAutoConfiguration.class);

    private ApplicationContext applicationContext;
    private RetrofitProperties properties;
    private Retrofit.Builder builder;

    public RetrofitAutoConfiguration(RetrofitProperties properties) {
        this.properties = properties;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean("connection-pool")
    @ConditionalOnMissingBean
    public ConnectionPool connectionPool() {
        logger.debug("Creating connection pool");
        return new ConnectionPool(properties.getConnection().getMaxIdleConnections(),
                properties.getConnection().getKeepAliveDuration(),
                TimeUnit.MINUTES);
    }

    @Bean("http-logging-interceptor")
    @ConditionalOnClass(HttpLoggingInterceptor.class)
    @ConditionalOnProperty(value = "retrofit.logging.enabled", havingValue = "true")
    public HttpLoggingInterceptor loggingInterceptor(RetrofitProperties properties) {
        Log logging = properties.getLogging();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(innerLogger(logging.getLevel()));
        return interceptor;
    }

    @Bean("ok-http-client")
    @DependsOn({"connection-pool", "http-logging-interceptor"})
    @ConditionalOnMissingBean
    public OkHttpClient okHttpClient(ConnectionPool connectionPool, HttpLoggingInterceptor httpLoggingInterceptor) {
        logger.debug("Creating okHttpClient");
        Connection connection = properties.getConnection();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .readTimeout(connection.getReadTimeout(), TimeUnit.MILLISECONDS)
                .writeTimeout(connection.getWriteTimeout(), TimeUnit.MILLISECONDS)
                .connectTimeout(connection.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .connectionPool(connectionPool);
        Set<Map.Entry<String, Interceptor>> entries = RetrofitInterceptorContextImpl.getInstance().entrySet();
        builder.addInterceptor(httpLoggingInterceptor);
        for (Map.Entry<String, Interceptor> entry : entries) {
            builder.addInterceptor(entry.getValue());
        }
        return builder.build();
    }


    @Bean("retrofit")
    @DependsOn({"ok-http-client"})
    @ConditionalOnMissingBean
    public Retrofit retrofit(OkHttpClient okHttpClient) {
        Retrofit.Builder builder = new Retrofit.Builder().validateEagerly(true);

        builder.baseUrl(properties.getEndpoint().getBaseUrl());

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssX")
                .setLenient()
                .create();
        builder.addConverterFactory(GsonConverterFactory.create(gson));

        if (okHttpClient != null) {
            builder.client(okHttpClient);
        }
        return builder.build();
    }

    private HttpLoggingInterceptor.Logger innerLogger(HttpLoggingInterceptor.Level level) {
        if (level == HttpLoggingInterceptor.Level.NONE) {
            return logger::error;
        } else if (level == HttpLoggingInterceptor.Level.BODY) {
            return logger::trace;
        } else if (level == HttpLoggingInterceptor.Level.HEADERS) {
            return logger::info;
        } else if (level == HttpLoggingInterceptor.Level.BASIC) {
            return logger::debug;
        }
        throw new UnsupportedOperationException("We don't support this log level currently.");
    }
}