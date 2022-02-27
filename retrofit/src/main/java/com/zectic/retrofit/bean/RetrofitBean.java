package com.zectic.retrofit.bean;

import com.zectic.retrofit.config.RetrofitProperties;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RetrofitBean {

    final private RetrofitProperties properties;

    public RetrofitBean(RetrofitProperties properties) {
        this.properties = properties;
    }
}
