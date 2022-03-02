package com.zestic.retrofit.interceptor;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * @author deebendukumar
 */
@Component
public class DecryptionInterceptor implements Interceptor {

    public DecryptionInterceptor() {
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        if (response.isSuccessful()) {
            Response.Builder newResponse = response.newBuilder();
            String contentType = response.header("Content-Type");
            if (StringUtils.isEmpty(contentType)) contentType = "application/json";
            String responseStr = response.body().string();
            String decrypted = null;

            /**
             * TODO implement your decryption here
             * Implement Observer and Observable pattern
             */

            newResponse.body(ResponseBody.create(MediaType.parse(contentType), decrypted));
            return newResponse.build();
        }
        return response;
    }
}
