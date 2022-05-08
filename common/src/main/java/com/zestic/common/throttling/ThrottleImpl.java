package com.zestic.common.throttling;

import java.util.Timer;
import java.util.TimerTask;

public class ThrottleImpl implements Throttler {

    private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(ThrottleImpl.class);

    private final String name;
    private final Integer throughput;
    private final Counter counter;

    public ThrottleImpl(String name, Integer throughput, Counter counter) {
        this.name = name;
        this.throughput = throughput;
        this.counter = counter;
    }

    @Override
    public void start() {

        new Timer(true).schedule(new TimerTask() {

            @Override
            public void run() {
                logger.info(name + " throughput [" + counter.get() + "]");
                counter.reset();
            }
        }, 0, 1000);
    }
}
