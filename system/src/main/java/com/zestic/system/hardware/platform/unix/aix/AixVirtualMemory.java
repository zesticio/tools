
package com.zestic.system.hardware.platform.unix.aix;

import com.sun.jna.platform.unix.aix.Perfstat.perfstat_memory_total_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractVirtualMemory;

import java.util.function.Supplier;

/*
 * Memory obtained by perfstat_memory_total_t
 */
@ThreadSafe final class AixVirtualMemory extends AbstractVirtualMemory {

    // AIX has multiple page size units, but for purposes of "pages" in perfstat,
    // the docs specify 4KB pages so we hardcode this
    private static final long PAGESIZE = 4096L;
    // Memoized perfstat from GlobalMemory
    private final Supplier<perfstat_memory_total_t> perfstatMem;

    /*
     * Constructor for SolarisVirtualMemory.
     *
     * @param perfstatMem The memoized perfstat data from the global memory class
     */
    AixVirtualMemory(Supplier<perfstat_memory_total_t> perfstatMem) {
        this.perfstatMem = perfstatMem;
    }

    @Override public long getSwapUsed() {
        perfstat_memory_total_t perfstat = perfstatMem.get();
        return (perfstat.pgsp_total - perfstat.pgsp_free) * PAGESIZE;
    }

    @Override public long getSwapTotal() {
        return perfstatMem.get().pgsp_total * PAGESIZE;
    }

    @Override public long getVirtualMax() {
        return perfstatMem.get().virt_total * PAGESIZE;
    }

    @Override public long getVirtualInUse() {
        return perfstatMem.get().virt_active * PAGESIZE;
    }

    @Override public long getSwapPagesIn() {
        return perfstatMem.get().pgspins;
    }

    @Override public long getSwapPagesOut() {
        return perfstatMem.get().pgspouts;
    }
}
