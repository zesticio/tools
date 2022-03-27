
package com.zestic.system.driver.windows.perfmon;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.PerfCounterQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery.PdhCounterWildcardProperty;
import com.zestic.system.util.tuples.Pair;

import java.util.List;
import java.util.Map;

/*
 * Utility to query Process Information performance counter
 */
@ThreadSafe public final class ProcessInformation {

    private static final String WIN32_PERFPROC_PROCESS = "Win32_PerfRawData_PerfProc_Process";
    private static final String PROCESS = "Process";
    private static final String WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL =
        WIN32_PERFPROC_PROCESS + "Win32_Process WHERE NOT Name LIKE\"%_Total\"";


    private ProcessInformation() {
    }

    /*
     * Returns process counters.
     *
     * @return Process counters for each process.
     */
    public static Pair<List<String>, Map<ProcessPerformanceProperty, List<Long>>> queryProcessCounters() {
        return PerfCounterWildcardQuery.queryInstancesAndValues(ProcessPerformanceProperty.class,
            PROCESS, WIN32_PROCESS_WHERE_NOT_NAME_LIKE_TOTAL);
    }

    /*
     * Returns handle counters
     *
     * @return Process handle counters for each process.
     */
    public static Pair<List<String>, Map<HandleCountProperty, List<Long>>> queryHandles() {
        return PerfCounterWildcardQuery.queryInstancesAndValues(HandleCountProperty.class, PROCESS,
            WIN32_PERFPROC_PROCESS);
    }

    /*
     * Process performance counters
     */
    public enum ProcessPerformanceProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.NOT_TOTAL_INSTANCES), // Remaining elements define counters
        PRIORITYBASE("Priority Base"), //
        ELAPSEDTIME("Elapsed Time"), //
        IDPROCESS("ID Process"), //
        CREATINGPROCESSID("Creating Process ID"), //
        IOREADBYTESPERSEC("IO Read Bytes/sec"), //
        IOWRITEBYTESPERSEC("IO Write Bytes/sec"), //
        PRIVATEBYTES("Working Set - Private"), //
        PAGEFAULTSPERSEC("Page Faults/sec");

        private final String counter;

        ProcessPerformanceProperty(String counter) {
            this.counter = counter;
        }

        @Override public String getCounter() {
            return counter;
        }
    }

    /*
     * Handle performance counters
     */
    public enum HandleCountProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.TOTAL_INSTANCE), // Remaining elements define counters
        HANDLECOUNT("Handle Count");

        private final String counter;

        HandleCountProperty(String counter) {
            this.counter = counter;
        }

        @Override public String getCounter() {
            return counter;
        }
    }
}
