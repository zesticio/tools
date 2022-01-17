package com.zectic.retrofit.stub;

import com.zectic.retrofit.converter.RequestBodyConverter;
import okhttp3.RequestBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class GithubReqBodyConverter implements RequestBodyConverter {
    @Override
    public RequestBody toRequestBody(@NotNull Object entity, Type type) {
        return null;
        //return RequestBody.create(JSON.toJSONBytes(entity), CONTENT_TYPE_JSON);
    }
}
