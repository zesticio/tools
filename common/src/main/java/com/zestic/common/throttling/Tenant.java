package com.zestic.common.throttling;

import lombok.Data;

import java.security.InvalidParameterException;

@Data
public class Tenant {

    private String name;
    private int throughput;

    public Tenant(String name, int throughput, Handler handler) {
        if (throughput < 0) {
            throw new InvalidParameterException("Number of calls less than 0 not allowed");
        }
        this.name = name;
        this.throughput = throughput;
        handler.addTenant(name);
    }
}
