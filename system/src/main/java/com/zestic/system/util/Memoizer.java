
package com.zestic.system.util;

import com.zestic.system.annotation.concurrent.ThreadSafe;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/*
 * A memoized function stores the output corresponding to some set of specific
 * inputs. Subsequent calls with remembered inputs return the remembered result
 * rather than recalculating it.
 */
@ThreadSafe public final class Memoizer {

    private static final Supplier<Long> defaultExpirationNanos =
        memoize(Memoizer::queryExpirationConfig, TimeUnit.MINUTES.toNanos(1));

    private Memoizer() {
    }

    private static long queryExpirationConfig() {
        return TimeUnit.MILLISECONDS.toNanos(
            GlobalConfig.get("com.zestic.system.util.memoizer.expiration", 300));
    }

    /*
     * Default exipiration of memoized values in nanoseconds, which will refresh
     * after this time elapses. Update by setting {@link GlobalConfig} property
     * <code>com.zestic.system.util.memoizer.expiration</code> to a value in milliseconds.
     *
     * @return The number of nanoseconds to keep memoized values before refreshing
     */
    public static long defaultExpiration() {
        return defaultExpirationNanos.get();
    }

    /*
     * Store a supplier in a delegate function to be computed once, and only again
     * after time to live (ttl) has expired.
     *
     * @param <T>      The type of object supplied
     * @param original The {@link Supplier} to memoize
     * @param ttlNanos Time in nanoseconds to retain calculation. If negative, retain
     *                 indefinitely.
     * @return A memoized version of the supplier
     */
    public static <T> Supplier<T> memoize(Supplier<T> original, long ttlNanos) {
        // Adapted from Guava's ExpiringMemoizingSupplier
        return new Supplier<T>() {
            final Supplier<T> delegate = original;
            volatile T value; // NOSONAR squid:S3077
            volatile long expirationNanos;

            @Override public T get() {
                long nanos = expirationNanos;
                long now = System.nanoTime();
                if (nanos == 0 || (ttlNanos >= 0 && now - nanos >= 0)) {
                    synchronized (this) {
                        if (nanos == expirationNanos) { // recheck for lost race
                            T t = delegate.get();
                            value = t;
                            nanos = now + ttlNanos;
                            expirationNanos = (nanos == 0) ? 1 : nanos;
                            return t;
                        }
                    }
                }
                return value;
            }
        };
    }

    /*
     * Store a supplier in a delegate function to be computed only once.
     *
     * @param <T>      The type of object supplied
     * @param original The {@link Supplier} to memoize
     * @return A memoized version of the supplier
     */
    public static <T> Supplier<T> memoize(Supplier<T> original) {
        return memoize(original, -1L);
    }
}
