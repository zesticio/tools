package com.zectic.retrofit.stub;

import com.zectic.retrofit.annotation.HttpClient;
import com.zectic.retrofit.annotation.HttpInterceptor;
import com.zectic.retrofit.annotation.ReplyConverterType;
import com.zectic.retrofit.annotation.RequestConverterType;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.POST;

@HttpClient("https://api.dev.50lion.com/web-app")
@RequestConverterType(GithubReqBodyConverter.class)
@ReplyConverterType(GithubReplyBodyConverter.class)
@HttpInterceptor(TestInterceptor1.class)
@HttpInterceptor(TestInterceptor2.class)
public interface GithubClient {

    @POST("account/login")
    @RequestConverterType(GithubReqBodyConverter.class)
    @ReplyConverterType(GithubReplyBodyConverter.class)
    Response<LoginVO> listRepos(@Body LoginForm form);
}
