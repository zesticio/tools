package com.zestic.retrofit.config;

import lombok.Data;

import java.io.Serializable;

/**
 * @author deebendukumar
 */
@Data
public class Connection implements Serializable {

    /**
     * # The maximum number of idle connections for each address.
     */
    private Integer maxIdleConnections = 16;

    /**
     * # The time (minutes) to live for each idle connections
     */
    private Integer keepAliveDuration = 2;

    private Integer retryTimes = 0;
    private Long readTimeout = 10000L;
    private Long writeTimeout = 10000L;
    private Long connectTimeout = 10000L;
}
