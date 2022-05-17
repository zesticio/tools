package com.zestic.common.ratelimit;

import java.util.concurrent.atomic.AtomicLong;

/**
 * We are using ReentrantReadWriteLock class of java an implementation of ReadWriteLock that also supports ReentrantLock functionality
 * <p>
 * The ReadWriteLock is a pair of associated locks one for read-only operation and one for writing. Even in a multi-threading application,
 * multiple reads can occur simultaneously for a shared resource. It is only when multiple writes happen simultaneously or intermix of
 * read and write that there is a chance of writing the wrong value or reading the wrong value.
 * <p>
 * Having a pair of read-write lock allows for a greater level of concurrency in accessing shared data than that permitted by a mutual
 * exclusion lock. It exploits the fact that while only a single thread at a time (a writer thread) can modify the shared data, in many
 * cases any number of threads can concurrently read the data (hence reader threads).
 * A read-write lock will improve performance over the use of a mutual exclusion lock if the frequency of reads is more than writes,
 * duration of the read operations is more than the duration of the writes. It also depends on the contention for the data – that is,
 * the number of threads that will try to read or write the data at the same time.
 * <p>
 * Basic idea is whenever a thread is trying to write or update the resource get a read lock. i.e thread who are waiting in a queue
 * to read is waiting for write to happen.
 */
public class Throttler extends Scheduler implements RateLimiter, RateLimiterListener {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(Throttler.class);

    private final AtomicLong bucket = new AtomicLong(0);
    private RateLimiterRule rule;

    public Throttler(RateLimiterRule rule) {
        this.rule = rule;
        init();
    }

    @Override
    public void init() {
    }

    @Override
    synchronized public boolean tryAcquire() {
        boolean allow = false;
        try {
            if (bucket.get() <= (rule.getLimit() - 1)) {
                allow = true;
            }
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
        }
        return allow;
    }

    @Override
    synchronized public void consume() {
        try {
            bucket.incrementAndGet();
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
        }
    }

    @Override
    synchronized public void refill() {
        try {
            bucket.set(0);
            logger.info("" + bucket.get());
        } catch (Exception ex) {
            logger.error("", ex);
        } finally {
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
