package com.zestic.common.ratelimit;

import com.zestic.common.utils.DateUtil;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TokenBucketRateLimiter extends Scheduler implements RateLimiter, RateLimiterListener {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TokenBucketRateLimiter.class);

    private final AtomicLong bucket = new AtomicLong(0);
    private com.google.common.util.concurrent.Monitor mutex = new com.google.common.util.concurrent.Monitor();
    private RateLimiterRule rule;

    public TokenBucketRateLimiter(RateLimiterRule rule) {
        this.rule = rule;
        init();
    }

    @Override
    public void init() {
    }

    @Override
    public boolean tryAcquire() {
        boolean allow = false;
        synchronized (mutex) {
            mutex.enter();
            try {
                if (bucket.get() >= 1) {
                    allow = true;
                } else {
                    try {
                        //wait for refill
                        mutex.wait();
                    } catch (InterruptedException e) {
                        logger.error("", e);
                    }
                }
            } finally {
                mutex.leave();
            }
        }
        return allow;
    }

    @Override
    public void consume() {
        synchronized (mutex) {
            bucket.decrementAndGet();
        }
    }

    @Override
    public void refill() {
        synchronized (mutex) {
            bucket.set(this.rule.getLimit());
            //notify waiting object to release
            mutex.notify();
        }
    }

    @Override
    public void start() {
        super.start(0, rule.getMonitor(), rule.getLimit(), this);
    }

    @Override
    public String getId() {
        return rule.getId();
    }
}
