
package com.zestic.system.driver.windows.perfmon;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.PerfCounterQuery;

import java.util.Map;

/*
 * Utility to query Paging File performance counter
 */
@ThreadSafe public final class PagingFile {

    private static final String PAGING_FILE = "Paging File";
    private static final String WIN32_PERF_RAW_DATA_PERF_OS_PAGING_FILE =
        "Win32_PerfRawData_PerfOS_PagingFile";


    private PagingFile() {
    }

    /*
     * Returns paging file counters
     *
     * @return Paging file counters for memory.
     */
    public static Map<PagingPercentProperty, Long> querySwapUsed() {
        return PerfCounterQuery.queryValues(PagingPercentProperty.class, PAGING_FILE,
            WIN32_PERF_RAW_DATA_PERF_OS_PAGING_FILE);
    }

    /*
     * For swap file usage
     */
    public enum PagingPercentProperty implements PerfCounterQuery.PdhCounterProperty {
        PERCENTUSAGE(PerfCounterQuery.TOTAL_INSTANCE, "% Usage");

        private final String instance;
        private final String counter;

        PagingPercentProperty(String instance, String counter) {
            this.instance = instance;
            this.counter = counter;
        }

        @Override public String getInstance() {
            return instance;
        }

        @Override public String getCounter() {
            return counter;
        }
    }
}
