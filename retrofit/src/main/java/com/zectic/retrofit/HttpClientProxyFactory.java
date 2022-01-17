package com.zectic.retrofit;

import com.zectic.retrofit.annotation.*;
import com.zectic.retrofit.calladapter.ResponseAdapterFactory;
import com.zectic.retrofit.interceptor.RequestTimeoutInterceptor;
import com.zectic.retrofit.proxy.StubProxyFactory;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.StringUtils;
import retrofit2.Retrofit;

import java.util.Comparator;
import java.util.Iterator;
import java.util.ServiceLoader;

@Slf4j
public class HttpClientProxyFactory implements StubProxyFactory, ApplicationContextAware {
    private static final ResponseAdapterFactory RESPONSE_ADAPTER_FACTORY = new ResponseAdapterFactory();
    private static final OkHttpClient BASE_HTTP_CLIENT;
    private ApplicationContext applicationContext;

    static {
        ServiceLoader<OkHttpClientLoader> loaders = ServiceLoader.load(OkHttpClientLoader.class);
        Iterator<OkHttpClientLoader> iterator = loaders.iterator();
        OkHttpClientLoader httpClientLoader;
        if (iterator.hasNext()) {
            httpClientLoader = iterator.next();
        } else {
            httpClientLoader = new DefaultOkHttpClientLoader();
        }
        BASE_HTTP_CLIENT = httpClientLoader.getBaseHttpClient();
    }

    @Override
    public <T> T createProxy(Class<T> stubInterface, ProxyStub stubAnnotation) {
        HttpClient httpClient = AnnotationUtils.getAnnotation(stubInterface, HttpClient.class);
        RequestConverterType reqConverterType = AnnotationUtils.getAnnotation(stubInterface, RequestConverterType.class);
        ReplyConverterType replyConverterType = AnnotationUtils.getAnnotation(stubInterface, ReplyConverterType.class);
        if (httpClient == null) {
            throw new NullPointerException(stubInterface.getName() + "上没有@httpClient注解");
        }
        Call.Factory callFactory = buildCallFactory(stubInterface);
        String baseUrl = buildBaseUrl(httpClient);
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .callFactory(callFactory)
                .addCallAdapterFactory(RESPONSE_ADAPTER_FACTORY)
                .addConverterFactory(new CompositeConverterFactory(applicationContext, reqConverterType, replyConverterType));
        log.debug("new Retrofit HttpClient.interface：" + stubInterface + " baseUrl：" + baseUrl);
        Retrofit retrofit = retrofitBuilder.build();
        return retrofit.create(stubInterface);
    }

    @NotNull
    private String buildBaseUrl(HttpClient httpClient) {
        String baseUrl = httpClient.contextPath();
        if (StringUtils.hasText(baseUrl)) {
            baseUrl = baseUrl.trim();
            baseUrl = applicationContext.getEnvironment().resolveRequiredPlaceholders(baseUrl);
            if (!baseUrl.endsWith("/")) {
                baseUrl += "/";
            }
        }
        return baseUrl;
    }

    @NotNull
    private <T> Call.Factory buildCallFactory(Class<T> stubInterface) {
        OkHttpClient.Builder builder = BASE_HTTP_CLIENT.newBuilder()
                .addInterceptor(new RequestTimeoutInterceptor());
        AnnotationUtils.getRepeatableAnnotations(stubInterface, HttpInterceptor.class)
                .stream()
                .sorted(Comparator.comparing(HttpInterceptor::index, Integer::compareTo))
                .map(it -> applicationContext.getBean(it.value()))
                .forEach(builder::addInterceptor);
        return (Call.Factory) builder.build();
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
