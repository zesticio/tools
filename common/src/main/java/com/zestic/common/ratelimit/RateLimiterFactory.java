package com.zestic.common.ratelimit;

public class RateLimiterFactory {

    public static RateLimiter of(RateLimiterRule rule) {
        RateLimiter limiterDefault = null;
        switch (rule.getLimiterModel()) {
            case LEAKY_BUCKET:
                limiterDefault = new TokenBucketRateLimiter(rule);
                return limiterDefault;
            case TOKEN_BUCKET:
                limiterDefault = new TokenBucketRateLimiter(rule);
                return limiterDefault;
            case MONITORING:
                limiterDefault = new Throttler(rule);
                return limiterDefault;
            case BUCKET4J:
                limiterDefault = new Bucket4JImpl(rule);
                return limiterDefault;
            default:
                limiterDefault = new TokenBucketRateLimiter(rule);
                return limiterDefault;
        }
    }

}
