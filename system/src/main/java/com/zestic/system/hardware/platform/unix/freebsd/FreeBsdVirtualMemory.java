
package com.zestic.system.hardware.platform.unix.freebsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractVirtualMemory;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.unix.freebsd.BsdSysctlUtil;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by swapinfo
 */
@ThreadSafe final class FreeBsdVirtualMemory extends AbstractVirtualMemory {

    private final Supplier<Long> used =
        memoize(FreeBsdVirtualMemory::querySwapUsed, defaultExpiration());
    private final Supplier<Long> total =
        memoize(FreeBsdVirtualMemory::querySwapTotal, defaultExpiration());
    private final Supplier<Long> pagesIn =
        memoize(FreeBsdVirtualMemory::queryPagesIn, defaultExpiration());
    private final Supplier<Long> pagesOut =
        memoize(FreeBsdVirtualMemory::queryPagesOut, defaultExpiration());
    FreeBsdGlobalMemory global;

    FreeBsdVirtualMemory(FreeBsdGlobalMemory freeBsdGlobalMemory) {
        this.global = freeBsdGlobalMemory;
    }

    private static long querySwapUsed() {
        String swapInfo = ExecutingCommand.getAnswerAt("swapinfo -k", 1);
        String[] split = ParseUtil.whitespaces.split(swapInfo);
        if (split.length < 5) {
            return 0L;
        }
        return ParseUtil.parseLongOrDefault(split[2], 0L) << 10;
    }

    private static long querySwapTotal() {
        return BsdSysctlUtil.sysctl("vm.swap_total", 0L);
    }

    private static long queryPagesIn() {
        return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsin", 0L);
    }

    private static long queryPagesOut() {
        return BsdSysctlUtil.sysctl("vm.stats.vm.v_swappgsout", 0L);
    }

    @Override public long getSwapUsed() {
        return used.get();
    }

    @Override public long getSwapTotal() {
        return total.get();
    }

    @Override public long getVirtualMax() {
        return this.global.getTotal() + getSwapTotal();
    }

    @Override public long getVirtualInUse() {
        return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
    }

    @Override public long getSwapPagesIn() {
        return pagesIn.get();
    }

    @Override public long getSwapPagesOut() {
        return pagesOut.get();
    }
}
