
package com.zestic.system.hardware.platform.unix.solaris;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.solaris.kstat.SystemPages;
import com.zestic.system.hardware.VirtualMemory;
import com.zestic.system.hardware.common.AbstractGlobalMemory;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.tuples.Pair;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by kstat
 */
@ThreadSafe final class SolarisGlobalMemory extends AbstractGlobalMemory {

    private final Supplier<Pair<Long, Long>> availTotal =
        memoize(SystemPages::queryAvailableTotal, defaultExpiration());

    private final Supplier<Long> pageSize = memoize(SolarisGlobalMemory::queryPageSize);

    private final Supplier<VirtualMemory> vm = memoize(this::createVirtualMemory);

    private static long queryPageSize() {
        return ParseUtil.parseLongOrDefault(ExecutingCommand.getFirstAnswer("pagesize"), 4096L);
    }

    @Override public long getAvailable() {
        return availTotal.get().getA() * getPageSize();
    }

    @Override public long getTotal() {
        return availTotal.get().getB() * getPageSize();
    }

    @Override public long getPageSize() {
        return pageSize.get();
    }

    @Override public VirtualMemory getVirtualMemory() {
        return vm.get();
    }

    private VirtualMemory createVirtualMemory() {
        return new SolarisVirtualMemory(this);
    }
}
