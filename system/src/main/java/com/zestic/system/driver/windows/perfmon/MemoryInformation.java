
package com.zestic.system.driver.windows.perfmon;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.PerfCounterQuery;

import java.util.Map;

/*
 * Utility to query Memory performance counter
 */
@ThreadSafe public final class MemoryInformation {

    private static final String MEMORY = "Memory";
    private static final String WIN32_PERF_RAW_DATA_PERF_OS_MEMORY =
        "Win32_PerfRawData_PerfOS_Memory";


    private MemoryInformation() {
    }

    /*
     * Returns page swap counters
     *
     * @return Page swap counters for memory.
     */
    public static Map<PageSwapProperty, Long> queryPageSwaps() {
        return PerfCounterQuery.queryValues(PageSwapProperty.class, MEMORY,
            WIN32_PERF_RAW_DATA_PERF_OS_MEMORY);
    }

    /*
     * For pages in/out
     */
    public enum PageSwapProperty implements PerfCounterQuery.PdhCounterProperty {
        PAGESINPUTPERSEC(null, "Pages Input/sec"), //
        PAGESOUTPUTPERSEC(null, "Pages Output/sec");

        private final String instance;
        private final String counter;

        PageSwapProperty(String instance, String counter) {
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
