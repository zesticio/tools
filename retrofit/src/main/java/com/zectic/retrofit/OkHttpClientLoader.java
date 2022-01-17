package com.zectic.retrofit;

import okhttp3.OkHttpClient;

public interface OkHttpClientLoader {
    OkHttpClient getBaseHttpClient();
}
