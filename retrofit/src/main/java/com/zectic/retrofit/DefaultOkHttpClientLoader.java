package com.zectic.retrofit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class DefaultOkHttpClientLoader implements OkHttpClientLoader {
    private static volatile OkHttpClient HTTP_CLIENT;

    @Override
    public OkHttpClient getBaseHttpClient() {
        if (HTTP_CLIENT == null) {
            synchronized (DefaultOkHttpClientLoader.class) {
                if (HTTP_CLIENT == null) {
                    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(HttpLogger::info);
                    interceptor.level(HttpLoggingInterceptor.Level.BODY);
                    HTTP_CLIENT = new OkHttpClient.Builder()
                            .addInterceptor(interceptor)
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return HTTP_CLIENT;
    }
}
