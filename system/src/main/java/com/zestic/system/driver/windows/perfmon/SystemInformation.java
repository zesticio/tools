
package com.zestic.system.driver.windows.perfmon;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.PerfCounterQuery;

import java.util.Map;

/*
 * Utility to query System performance counter
 */
@ThreadSafe public final class SystemInformation {

    private static final String SYSTEM = "System";
    private static final String WIN32_PERF_RAW_DATA_PERF_OS_SYSTEM =
        "Win32_PerfRawData_PerfOS_System";


    private SystemInformation() {
    }

    /*
     * Returns system context switch counters.
     *
     * @return Context switches counter for the total of all processors.
     */
    public static Map<ContextSwitchProperty, Long> queryContextSwitchCounters() {
        return PerfCounterQuery.queryValues(ContextSwitchProperty.class, SYSTEM,
            WIN32_PERF_RAW_DATA_PERF_OS_SYSTEM);
    }

    /*
     * Context switch property
     */
    public enum ContextSwitchProperty implements PerfCounterQuery.PdhCounterProperty {
        CONTEXTSWITCHESPERSEC(null, "Context Switches/sec");

        private final String instance;
        private final String counter;

        ContextSwitchProperty(String instance, String counter) {
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
