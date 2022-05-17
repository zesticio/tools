package com.zestic.common.ratelimit;

import com.zestic.common.exception.NotImplementedException;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class Bucket4JImpl extends Scheduler implements RateLimiter, RateLimiterListener {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Bucket4JImpl.class);

    private com.google.common.util.concurrent.Monitor mutex = new com.google.common.util.concurrent.Monitor();
    private final AtomicLong counter = new AtomicLong(0);
    private RateLimiterRule rule;
    private Refill refill;
    private Bandwidth limit;
    private Bucket bucket;

    public Bucket4JImpl(RateLimiterRule rule) {
        this.rule = rule;
        init();
    }

    @Override
    public String getId() {
        return this.rule.getId();
    }

    @Override
    public void init() {
        refill = Refill.intervally(this.rule.getLimit(), Duration.ofSeconds(rule.getPeriod()));
        limit = Bandwidth.classic(this.rule.getLimit(), refill);
        bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    public boolean tryAcquire() {
        boolean allow = false;
        synchronized (mutex) {
            try {
                mutex.enter();
                allow = bucket.tryConsume(1);
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                mutex.leave();
            }
        }
        return allow;
    }

    @Override
    public void consume() {
        synchronized (mutex) {
            try {
                mutex.enter();
                counter.incrementAndGet();
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                mutex.leave();
            }
        }
    }

    @Override
    public void refill() {
        synchronized (mutex) {
            try {
                mutex.enter();
                logger.info("Counter " + counter.get());
                counter.set(0);
            } catch (Exception ex) {
                logger.error("", ex);
            } finally {
                mutex.leave();
            }
        }
    }

    @Override
    public void start() {
        super.start(0, rule.getMonitor(), rule.getLimit(), this);
    }
}
