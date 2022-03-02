package com.zestic.retrofit.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author deebendukumar
 */
@Data
@ConfigurationProperties(prefix = "retrofit")
public class RetrofitProperties implements Serializable {

    private Integer timeout = 5000;
    @NestedConfigurationProperty
    private Connection connection;
    @NestedConfigurationProperty
    private Log logging;
    @NestedConfigurationProperty
    private List<Endpoint> endpoints = new ArrayList<>();
    @NestedConfigurationProperty
    private Endpoint endpoint;
}
