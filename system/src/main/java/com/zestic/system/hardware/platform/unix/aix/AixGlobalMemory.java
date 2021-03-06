
package com.zestic.system.hardware.platform.unix.aix;

import com.sun.jna.platform.unix.aix.Perfstat.perfstat_memory_total_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.aix.perfstat.PerfstatMemory;
import com.zestic.system.hardware.PhysicalMemory;
import com.zestic.system.hardware.VirtualMemory;
import com.zestic.system.hardware.common.AbstractGlobalMemory;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ParseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by perfstat_memory_total_t
 */
@ThreadSafe final class AixGlobalMemory extends AbstractGlobalMemory {

    // AIX has multiple page size units, but for purposes of "pages" in perfstat,
    // the docs specify 4KB pages so we hardcode this
    private static final long PAGESIZE = 4096L;
    private final Supplier<perfstat_memory_total_t> perfstatMem =
        memoize(AixGlobalMemory::queryPerfstat, defaultExpiration());
    private final Supplier<List<String>> lscfg;
    private final Supplier<VirtualMemory> vm = memoize(this::createVirtualMemory);

    AixGlobalMemory(Supplier<List<String>> lscfg) {
        this.lscfg = lscfg;
    }

    private static perfstat_memory_total_t queryPerfstat() {
        return PerfstatMemory.queryMemoryTotal();
    }

    @Override public long getAvailable() {
        return perfstatMem.get().real_avail * PAGESIZE;
    }

    @Override public long getTotal() {
        return perfstatMem.get().real_total * PAGESIZE;
    }

    @Override public long getPageSize() {
        return PAGESIZE;
    }

    @Override public VirtualMemory getVirtualMemory() {
        return vm.get();
    }

    @Override public List<PhysicalMemory> getPhysicalMemory() {
        List<PhysicalMemory> pmList = new ArrayList<>();
        boolean isMemModule = false;
        String bankLabel = Constants.UNKNOWN;
        String locator = "";
        long capacity = 0L;
        for (String line : lscfg.get()) {
            String s = line.trim();
            if (s.endsWith("memory-module")) {
                isMemModule = true;
            } else if (isMemModule) {
                if (s.startsWith("Node:")) {
                    bankLabel = s.substring(5).trim();
                    if (bankLabel.startsWith("IBM,")) {
                        bankLabel = bankLabel.substring(4);
                    }
                } else if (s.startsWith("Physical Location:")) {
                    locator = "/" + s.substring(18).trim();
                } else if (s.startsWith("Size")) {
                    capacity = ParseUtil.parseLongOrDefault(
                        ParseUtil.removeLeadingDots(s.substring(4).trim()), 0L) << 20;
                } else if (s.startsWith("Hardware Location Code")) {
                    // Save previous bank
                    if (capacity > 0) {
                        pmList.add(new PhysicalMemory(bankLabel + locator, capacity, 0L, "IBM",
                            Constants.UNKNOWN));
                    }
                    bankLabel = Constants.UNKNOWN;
                    locator = "";
                    capacity = 0L;
                    isMemModule = false;
                }
            }
        }
        return pmList;
    }

    private VirtualMemory createVirtualMemory() {
        return new AixVirtualMemory(perfstatMem);
    }
}
