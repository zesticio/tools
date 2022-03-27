
package com.zestic.system.driver.windows.perfmon;

import com.sun.jna.platform.win32.VersionHelpers;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.PerfCounterQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery.PdhCounterWildcardProperty;
import com.zestic.system.util.tuples.Pair;

import java.util.List;
import java.util.Map;

/*
 * Utility to query Processor performance counter
 */
@ThreadSafe public final class ProcessorInformation {

    private static final String PROCESSOR = "Processor";
    private static final String PROCESSOR_INFORMATION = "Processor Information";

    // For Win7+ ... NAME field includes NUMA nodes
    private static final String
        WIN32_PERF_RAW_DATA_COUNTERS_PROCESSOR_INFORMATION_WHERE_NOT_NAME_LIKE_TOTAL =
        "Win32_PerfRawData_Counters_ProcessorInformation WHERE NOT Name LIKE \"%_Total\"";

    // For Vista- ... Older systems just have processor #
    private static final String WIN32_PERF_RAW_DATA_PERF_OS_PROCESSOR_WHERE_NOT_NAME_TOTAL =
        "Win32_PerfRawData_PerfOS_Processor WHERE NOT Name=\"_Total\"";
    private static final String WIN32_PERF_RAW_DATA_PERF_OS_PROCESSOR_WHERE_NAME_TOTAL =
        "Win32_PerfRawData_PerfOS_Processor WHERE Name=\"_Total\"";

    private static final boolean IS_WIN7_OR_GREATER = VersionHelpers.IsWindows7OrGreater();


    private ProcessorInformation() {
    }

    /*
     * Returns processor performance counters.
     *
     * @return Performance Counters for processors.
     */
    public static Pair<List<String>, Map<ProcessorTickCountProperty, List<Long>>> queryProcessorCounters() {
        return IS_WIN7_OR_GREATER ?
            PerfCounterWildcardQuery.queryInstancesAndValues(ProcessorTickCountProperty.class,
                PROCESSOR_INFORMATION,
                WIN32_PERF_RAW_DATA_COUNTERS_PROCESSOR_INFORMATION_WHERE_NOT_NAME_LIKE_TOTAL) :
            PerfCounterWildcardQuery.queryInstancesAndValues(ProcessorTickCountProperty.class,
                PROCESSOR, WIN32_PERF_RAW_DATA_PERF_OS_PROCESSOR_WHERE_NOT_NAME_TOTAL);
    }

    /*
     * Returns system interrupts counters.
     *
     * @return Interrupts counter for the total of all processors.
     */
    public static Map<InterruptsProperty, Long> queryInterruptCounters() {
        return PerfCounterQuery.queryValues(InterruptsProperty.class, PROCESSOR,
            WIN32_PERF_RAW_DATA_PERF_OS_PROCESSOR_WHERE_NAME_TOTAL);
    }

    /*
     * Returns processor frequency counters.
     *
     * @return Processor frequency counter for each processor.
     */
    public static Pair<List<String>, Map<ProcessorFrequencyProperty, List<Long>>> queryFrequencyCounters() {
        return PerfCounterWildcardQuery.queryInstancesAndValues(ProcessorFrequencyProperty.class,
            PROCESSOR_INFORMATION,
            WIN32_PERF_RAW_DATA_COUNTERS_PROCESSOR_INFORMATION_WHERE_NOT_NAME_LIKE_TOTAL);
    }

    /*
     * Processor performance counters
     */
    public enum ProcessorTickCountProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.NOT_TOTAL_INSTANCES), // Remaining elements define counters
        PERCENTDPCTIME("% DPC Time"), //
        PERCENTINTERRUPTTIME("% Interrupt Time"), //
        PERCENTPRIVILEGEDTIME("% Privileged Time"), //
        PERCENTPROCESSORTIME("% Processor Time"), //
        PERCENTUSERTIME("% User Time");

        private final String counter;

        ProcessorTickCountProperty(String counter) {
            this.counter = counter;
        }

        @Override public String getCounter() {
            return counter;
        }
    }

    /*
     * System interrupts counters
     */
    public enum InterruptsProperty implements PerfCounterQuery.PdhCounterProperty {
        INTERRUPTSPERSEC(PerfCounterQuery.TOTAL_INSTANCE, "Interrupts/sec");

        private final String instance;
        private final String counter;

        InterruptsProperty(String instance, String counter) {
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

    /*
     * Processor Frequency counters. Requires Win7 or greater
     */
    public enum ProcessorFrequencyProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.NOT_TOTAL_INSTANCES), // Remaining elements define counters
        PERCENTOFMAXIMUMFREQUENCY("% of Maximum Frequency");

        private final String counter;

        ProcessorFrequencyProperty(String counter) {
            this.counter = counter;
        }

        @Override public String getCounter() {
            return counter;
        }
    }
}
