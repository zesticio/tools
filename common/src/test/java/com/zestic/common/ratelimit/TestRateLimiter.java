package com.zestic.common.ratelimit;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

public class TestRateLimiter {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(TestRateLimiter.class);

    @Test
    public void testTokenBucketRateLimiter() {
        RateLimiterRule rateLimiterRule = new RateLimiterRuleBuilder()
                .setLimit(1)
                .setPeriod(10)
                .setUnit(TimeUnit.SECONDS)
                .setLimiterModel(LimiterModel.TOKEN_BUCKET)
                .build();
        RateLimiter limiter = RateLimiterFactory.of(rateLimiterRule);
        limiter.start();
        while (true) {
            if (limiter.tryAcquire()) {
                logger.debug("Consumed");
                limiter.consume();
            }
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testMonitor() {
        RateLimiterRule rateLimiterRule = new RateLimiterRuleBuilder()
                .setLimit(100)
                .setPeriod(1)
                .setUnit(TimeUnit.SECONDS)
                .setLimiterModel(LimiterModel.MONITORING)
                .build();
        RateLimiter limiter = RateLimiterFactory.of(rateLimiterRule);
        limiter.start();
        while (true) {
            try {
                if (limiter.tryAcquire()) {
                    logger.debug("Consumed");
                    limiter.consume();
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Test
    public void testBucket4j() {
        RateLimiterRule rateLimiterRule = new RateLimiterRuleBuilder()
                .setLimit(100)
                .setPeriod(1)
                .setUnit(TimeUnit.SECONDS)
                .setLimiterModel(LimiterModel.BUCKET4J)
                .build();
        RateLimiter limiter = RateLimiterFactory.of(rateLimiterRule);
        limiter.start();
        while (true) {
            try {
                if (limiter.tryAcquire()) {
                    logger.debug("Consumed");
                    limiter.consume();
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
