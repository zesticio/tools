
package com.zestic.system.driver.windows.perfmon;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.PerfCounterQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery;
import com.zestic.system.util.platform.windows.PerfCounterWildcardQuery.PdhCounterWildcardProperty;
import com.zestic.system.util.tuples.Pair;

import java.util.List;
import java.util.Map;

/*
 * Utility to query PhysicalDisk performance counter
 */
@ThreadSafe public final class PhysicalDisk {

    private static final String PHYSICAL_DISK = "PhysicalDisk";
    private static final String WIN32_PERF_RAW_DATA_PERF_DISK_PHYSICAL_DISK_WHERE_NOT_NAME_TOTAL =
        "Win32_PerfRawData_PerfDisk_PhysicalDisk WHERE NOT Name=\"_Total\"";


    private PhysicalDisk() {
    }

    /*
     * Returns physical disk performance counters.
     *
     * @return Performance Counters for physical disks.
     */
    public static Pair<List<String>, Map<PhysicalDiskProperty, List<Long>>> queryDiskCounters() {
        return PerfCounterWildcardQuery.queryInstancesAndValues(PhysicalDiskProperty.class,
            PHYSICAL_DISK, WIN32_PERF_RAW_DATA_PERF_DISK_PHYSICAL_DISK_WHERE_NOT_NAME_TOTAL);
    }

    /*
     * Physical Disk performance counters.
     */
    public enum PhysicalDiskProperty implements PdhCounterWildcardProperty {
        // First element defines WMI instance name field and PDH instance filter
        NAME(PerfCounterQuery.NOT_TOTAL_INSTANCE), // Remaining elements define counters
        DISKREADSPERSEC("Disk Reads/sec"), //
        DISKREADBYTESPERSEC("Disk Read Bytes/sec"), //
        DISKWRITESPERSEC("Disk Writes/sec"), //
        DISKWRITEBYTESPERSEC("Disk Write Bytes/sec"), //
        CURRENTDISKQUEUELENGTH("Current Disk Queue Length"), //
        PERCENTDISKTIME("% Disk Time");

        private final String counter;

        PhysicalDiskProperty(String counter) {
            this.counter = counter;
        }

        @Override public String getCounter() {
            return counter;
        }
    }
}
