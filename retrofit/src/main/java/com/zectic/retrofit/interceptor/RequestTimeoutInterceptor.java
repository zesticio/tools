package com.zectic.retrofit.interceptor;

import com.zectic.retrofit.annotation.RequestTimeout;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class RequestTimeoutInterceptor extends BaseMethodAnnotationInterceptor<RequestTimeout> {

    public RequestTimeoutInterceptor() {
        super(RequestTimeout.class);
    }

    @Override
    protected Response doIntercept(@NotNull Method method, @NotNull RequestTimeout annotation, @NotNull Chain chain, @NotNull Request request) throws IOException {
        int connectTimeout = annotation.connectTimeout();
        if (connectTimeout > 0) {
            chain = chain.withConnectTimeout(connectTimeout, TimeUnit.SECONDS);
        }
        int readTimeout = annotation.readTimeout();
        if (readTimeout > 0) {
            chain = chain.withReadTimeout(readTimeout, TimeUnit.SECONDS);
        }
        int writeTimeout = annotation.writeTimeout();
        if (writeTimeout > 0) {
            chain = chain.withWriteTimeout(writeTimeout, TimeUnit.SECONDS);
        }
        return chain.proceed(request);
    }
}
