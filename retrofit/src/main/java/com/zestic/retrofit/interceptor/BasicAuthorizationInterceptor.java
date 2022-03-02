package com.zestic.retrofit.interceptor;

import lombok.AllArgsConstructor;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author deebendukumar
 */
@Component
@AllArgsConstructor
public class BasicAuthorizationInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Basic " + "token")
                .build();
        Response response = chain.proceed(request);
        return response;
    }
}
