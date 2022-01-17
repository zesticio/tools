package com.zectic.retrofit.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zectic.retrofit.converter.ReplyBodyConverter;
import okhttp3.ResponseBody;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

@Component
public class GithubReplyBodyConverter implements ReplyBodyConverter {

    @Override
    public Object fromResponseBody(@NotNull ResponseBody body, Type type) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValueAsString(body.string());
        return null;
        //return JSON.parseObject(body.string(), type);
    }
}
