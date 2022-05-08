package com.zestic.common.throttling;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public final class Counter {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Counter.class);

    private final long RATE_INTERVAL_MILLISECONDS;
    private AtomicInteger index;

    public Counter() {
        index = new AtomicInteger(0);
        RATE_INTERVAL_MILLISECONDS = TimeUnit.SECONDS.toMillis(1);
    }

    public Integer increment() {
        return index.incrementAndGet();
    }

    public Integer get() {
        return index.get();
    }

    public void reset() {
        index = new AtomicInteger(0);
    }
}
