
package com.zestic.system.hardware.platform.unix.openbsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractVirtualMemory;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.tuples.Triplet;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory info on OpenBSD
 */
@ThreadSafe final class OpenBsdVirtualMemory extends AbstractVirtualMemory {

    private final Supplier<Triplet<Integer, Integer, Integer>> usedTotalPgin =
        memoize(OpenBsdVirtualMemory::queryVmstat, defaultExpiration());
    private final Supplier<Integer> pgout =
        memoize(OpenBsdVirtualMemory::queryUvm, defaultExpiration());
    OpenBsdGlobalMemory global;

    OpenBsdVirtualMemory(OpenBsdGlobalMemory freeBsdGlobalMemory) {
        this.global = freeBsdGlobalMemory;
    }

    private static Triplet<Integer, Integer, Integer> queryVmstat() {
        int used = 0;
        int total = 0;
        int swapIn = 0;
        for (String line : ExecutingCommand.runNative("vmstat -s")) {
            if (line.contains("swap pages in use")) {
                used = ParseUtil.getFirstIntValue(line);
            } else if (line.contains("swap pages")) {
                total = ParseUtil.getFirstIntValue(line);
            } else if (line.contains("pagein operations")) {
                swapIn = ParseUtil.getFirstIntValue(line);
            }
        }
        return new Triplet<>(used, total, swapIn);
    }

    private static int queryUvm() {
        for (String line : ExecutingCommand.runNative("systat -ab uvm")) {
            if (line.contains("pdpageouts")) {
                // First column is non-numeric "Constants" header
                return ParseUtil.getFirstIntValue(line);
            }
        }
        return 0;
    }

    @Override public long getSwapUsed() {
        return usedTotalPgin.get().getA() * global.getPageSize();
    }

    @Override public long getSwapTotal() {
        return usedTotalPgin.get().getB() * global.getPageSize();
    }

    @Override public long getVirtualMax() {
        return this.global.getTotal() + getSwapTotal();
    }

    @Override public long getVirtualInUse() {
        return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
    }

    @Override public long getSwapPagesIn() {
        return usedTotalPgin.get().getC() * global.getPageSize();
    }

    @Override public long getSwapPagesOut() {
        return pgout.get() * global.getPageSize();
    }
}
