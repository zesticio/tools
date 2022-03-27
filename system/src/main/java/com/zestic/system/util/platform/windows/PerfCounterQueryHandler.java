
package com.zestic.system.util.platform.windows;

import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.zestic.system.annotation.concurrent.NotThreadSafe;
import com.zestic.system.util.FormatUtil;
import org.apache.log4j.Priority;

import java.util.HashMap;
import java.util.Map;

/*
 * Utility to handle Performance Counter Queries
 * <p>
 * This class is not thread safe. Each query handler instance should only be
 * used in a single thread, preferably in a try-with-resources block.
 */
@NotThreadSafe
public final class PerfCounterQueryHandler implements AutoCloseable {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.LogManager.getLogger(PerfCounterQuery.class);

    // Map of counter handles
    private Map<PerfDataUtil.PerfCounter, HANDLEByReference> counterHandleMap = new HashMap<>();
    // The query handle
    private HANDLEByReference queryHandle = null;

    /*
     * Begin monitoring a Performance Data counter.
     *
     * @param counter A PerfCounter object.
     * @return True if the counter was successfully added to the query.
     */
    public boolean addCounterToQuery(PerfDataUtil.PerfCounter counter) {
        // Open a new query or get the handle to an existing one
        if (this.queryHandle == null) {
            this.queryHandle = new HANDLEByReference();
            if (!PerfDataUtil.openQuery(this.queryHandle)) {
                LOG.warn("Failed to open a query for PDH counter: {" + counter.getCounterPath() + "}");
                this.queryHandle = null;
                return false;
            }
        }
        // Get a new handle for the counter
        HANDLEByReference p = new HANDLEByReference();
        if (!PerfDataUtil.addCounter(this.queryHandle, counter.getCounterPath(), p)) {
            LOG.warn("Failed to add counter for PDH counter: {" + counter.getCounterPath() + "}");
            return false;
        }
        counterHandleMap.put(counter, p);
        return true;
    }

    /*
     * Stop monitoring a Performance Data counter.
     *
     * @param counter A PerfCounter object
     * @return True if the counter was successfully removed.
     */
    public boolean removeCounterFromQuery(PerfDataUtil.PerfCounter counter) {
        boolean success = false;
        HANDLEByReference href = counterHandleMap.remove(counter);
        // null if handle wasn't present
        if (href != null) {
            success = PerfDataUtil.removeCounter(href);
        }
        if (counterHandleMap.isEmpty()) {
            PerfDataUtil.closeQuery(queryHandle);
            queryHandle = null;
        }
        return success;
    }

    /*
     * Stop monitoring all Performance Data counters and release their resources
     */
    public void removeAllCounters() {
        // Remove all counters from counterHandle map
        for (HANDLEByReference href : counterHandleMap.values()) {
            PerfDataUtil.removeCounter(href);
        }
        counterHandleMap.clear();
        // Remove query
        if (this.queryHandle != null) {
            PerfDataUtil.closeQuery(this.queryHandle);
        }
        this.queryHandle = null;
    }

    /*
     * Update all counters on this query.
     *
     * @return The timestamp for the update of all the counters, in milliseconds
     * since the epoch, or 0 if the update failed
     */
    public long updateQuery() {
        if (queryHandle == null) {
            LOG.warn("Query does not exist to update.");
            return 0L;
        }
        return PerfDataUtil.updateQueryTimestamp(queryHandle);
    }

    /*
     * Query the raw counter value of a Performance Data counter. Further
     * mathematical manipulation/conversion is left to the caller.
     *
     * @param counter The counter to query
     * @return The raw value of the counter
     */
    public long queryCounter(PerfDataUtil.PerfCounter counter) {
        if (!counterHandleMap.containsKey(counter)) {
            if (LOG.isEnabledFor(Priority.ERROR)) {
                LOG.warn("Counter {} does not exist to query." + counter.getCounterPath());
            }
            return 0;
        }
        long value = PerfDataUtil.queryCounter(counterHandleMap.get(counter));
        if (value < 0) {
            if (LOG.isEnabledFor(Priority.ERROR)) {
                LOG.warn("Error querying counter {" + counter.getCounterPath() + "}: {" + String.format(FormatUtil.formatError((int) value)) + "}");
            }
            return 0L;
        }
        return value;
    }

    @Override
    public void close() {
        removeAllCounters();
    }
}
