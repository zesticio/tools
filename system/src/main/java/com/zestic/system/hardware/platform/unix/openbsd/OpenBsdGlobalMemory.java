
package com.zestic.system.hardware.platform.unix.openbsd;

import com.sun.jna.Memory;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.VirtualMemory;
import com.zestic.system.hardware.common.AbstractGlobalMemory;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.unix.openbsd.OpenBsdSysctlUtil;

import java.util.function.Supplier;

import static com.zestic.system.jna.platform.unix.openbsd.OpenBsdLibc.*;
import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by sysctl vm.stats
 */
@ThreadSafe final class OpenBsdGlobalMemory extends AbstractGlobalMemory {

    private final Supplier<Long> available =
        memoize(OpenBsdGlobalMemory::queryAvailable, defaultExpiration());

    private final Supplier<Long> total = memoize(OpenBsdGlobalMemory::queryPhysMem);

    private final Supplier<Long> pageSize = memoize(OpenBsdGlobalMemory::queryPageSize);

    private final Supplier<VirtualMemory> vm = memoize(this::createVirtualMemory);

    private static long queryAvailable() {
        long free = 0L;
        long inactive = 0L;
        for (String line : ExecutingCommand.runNative("vmstat -s")) {
            if (line.endsWith("pages free")) {
                free = ParseUtil.getFirstIntValue(line);
            } else if (line.endsWith("pages inactive")) {
                inactive = ParseUtil.getFirstIntValue(line);
            }
        }
        int[] mib = new int[3];
        mib[0] = CTL_VFS;
        mib[1] = VFS_GENERIC;
        mib[2] = VFS_BCACHESTAT;
        Memory m = OpenBsdSysctlUtil.sysctl(mib);
        Bcachestats cache = new Bcachestats(m);
        return (cache.numbufpages + free + inactive);
    }

    private static long queryPhysMem() {
        return OpenBsdSysctlUtil.sysctl("hw.physmem", 0L);
    }

    private static long queryPageSize() {
        return OpenBsdSysctlUtil.sysctl("hw.pagesize", 4096L);
    }

    @Override public long getAvailable() {
        return available.get() * getPageSize();
    }

    @Override public long getTotal() {
        return total.get();
    }

    @Override public long getPageSize() {
        return pageSize.get();
    }

    @Override public VirtualMemory getVirtualMemory() {
        return vm.get();
    }

    private VirtualMemory createVirtualMemory() {
        return new OpenBsdVirtualMemory(this);
    }
}
