
package com.zestic.system.driver.windows.perfmon;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.PerfCounterQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery.PdhCounterWildcardProperty;
import com.zestic.system.util.tuples.Pair;

import java.util.List;
import java.util.Map;

/*
 * Utility to query Thread Information performance counter
 */
@ThreadSafe public final class ThreadInformation {

    private static final String THREAD = "Thread";
    private static final String WIN32_PERF_RAW_DATA_PERF_PROC_THREAD =
        "Win32_PerfRawData_PerfProc_Thread WHERE NOT Name LIKE \"%_Total\"";


    private ThreadInformation() {
    }

    /*
     * Returns thread counters.
     *
     * @return Thread counters for each thread.
     */
    public static Pair<List<String>, Map<ThreadPerformanceProperty, List<Long>>> queryThreadCounters() {
        return PerfCounterWildcardQuery.queryInstancesAndValues(ThreadPerformanceProperty.class,
            THREAD, WIN32_PERF_RAW_DATA_PERF_PROC_THREAD);
    }

    /*
     * Thread performance counters
     */
    public enum ThreadPerformanceProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.NOT_TOTAL_INSTANCES), // Remaining elements define counters
        PERCENTUSERTIME("% User Time"), //
        PERCENTPRIVILEGEDTIME("% Privileged Time"), //
        ELAPSEDTIME("Elapsed Time"), //
        PRIORITYCURRENT("Priority Current"), //
        STARTADDRESS("Start Address"), //
        THREADSTATE("Thread State"), //
        THREADWAITREASON("Thread Wait Reason"), // 5 is SUSPENDED
        IDPROCESS("ID Process"), //
        IDTHREAD("ID Thread"), //
        CONTEXTSWITCHESPERSEC("Context Switches/sec");

        private final String counter;

        ThreadPerformanceProperty(String counter) {
            this.counter = counter;
        }

        @Override public String getCounter() {
            return counter;
        }
    }
}
