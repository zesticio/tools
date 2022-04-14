package com.zestic.common.throttling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class Handler {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getSimpleName());
    private Map<String, AtomicLong> map = new ConcurrentHashMap<>();

    public void addTenant(String name) {
        map.putIfAbsent(name, new AtomicLong(0));
    }

    public void incrementCount(String name) {
        map.get(name).incrementAndGet();
    }

    public long getCount(String name) {
        return map.get(name).get();
    }

    public void reset(String name) {
        System.out.println("Resetting the map, throughout [ " + name + " ] [" + getCount(name) + "]");
        map.replaceAll((k, v) -> new AtomicLong(0));
    }
}
