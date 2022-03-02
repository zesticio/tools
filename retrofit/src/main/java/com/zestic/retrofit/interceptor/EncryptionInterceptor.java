package com.zestic.retrofit.interceptor;

import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author deebendukumar
 */
@Component
public class EncryptionInterceptor implements Interceptor {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger(EncryptionInterceptor.class);

    public EncryptionInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();
        RequestBody raw = request.body();
        String encrypted = "";
        MediaType mediaType = MediaType.parse("text/plain; charset=utf-8");

        /**
         * do your encryption implementation here
         * Implement Observer and Observable pattern
         */

        RequestBody body = RequestBody.create(mediaType, encrypted);
        request = request.newBuilder().header("Content-Type", body.contentType().toString())
                .header("Content-Length", String.valueOf(body.contentLength()))
                .method(request.method(), body).build();
        return chain.proceed(request);
    }
}
