
package com.zestic.system.hardware.platform.unix.solaris;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.solaris.kstat.SystemPages;
import com.zestic.system.hardware.common.AbstractVirtualMemory;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.tuples.Pair;

import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by kstat and swap
 */
@ThreadSafe final class SolarisVirtualMemory extends AbstractVirtualMemory {

    private static final Pattern SWAP_INFO = Pattern.compile(".+\\s(\\d+)K\\s+(\\d+)K$");

    private final SolarisGlobalMemory global;

    // Physical
    private final Supplier<Pair<Long, Long>> availTotal =
        memoize(SystemPages::queryAvailableTotal, defaultExpiration());

    // Swap
    private final Supplier<Pair<Long, Long>> usedTotal =
        memoize(SolarisVirtualMemory::querySwapInfo, defaultExpiration());

    private final Supplier<Long> pagesIn =
        memoize(SolarisVirtualMemory::queryPagesIn, defaultExpiration());

    private final Supplier<Long> pagesOut =
        memoize(SolarisVirtualMemory::queryPagesOut, defaultExpiration());

    /*
     * Constructor for SolarisVirtualMemory.
     *
     * @param solarisGlobalMemory The parent global memory class instantiating this
     */
    SolarisVirtualMemory(SolarisGlobalMemory solarisGlobalMemory) {
        this.global = solarisGlobalMemory;
    }

    private static long queryPagesIn() {
        long swapPagesIn = 0L;
        for (String s : ExecutingCommand.runNative("kstat -p cpu_stat:::pgswapin")) {
            swapPagesIn += ParseUtil.parseLastLong(s, 0L);
        }
        return swapPagesIn;
    }

    private static long queryPagesOut() {
        long swapPagesOut = 0L;
        for (String s : ExecutingCommand.runNative("kstat -p cpu_stat:::pgswapout")) {
            swapPagesOut += ParseUtil.parseLastLong(s, 0L);
        }
        return swapPagesOut;
    }

    private static Pair<Long, Long> querySwapInfo() {
        long swapTotal = 0L;
        long swapUsed = 0L;
        String swap = ExecutingCommand.getAnswerAt("swap -lk", 1);
        Matcher m = SWAP_INFO.matcher(swap);
        if (m.matches()) {
            swapTotal = ParseUtil.parseLongOrDefault(m.group(1), 0L) << 10;
            swapUsed = swapTotal - (ParseUtil.parseLongOrDefault(m.group(2), 0L) << 10);
        }
        return new Pair<>(swapUsed, swapTotal);
    }

    @Override public long getSwapUsed() {
        return usedTotal.get().getA();
    }

    @Override public long getSwapTotal() {
        return usedTotal.get().getB();
    }

    @Override public long getVirtualMax() {
        return this.global.getPageSize() * availTotal.get().getB() + getSwapTotal();
    }

    @Override public long getVirtualInUse() {
        return this.global.getPageSize() * (availTotal.get().getB() - availTotal.get().getA())
            + getSwapUsed();
    }

    @Override public long getSwapPagesIn() {
        return pagesIn.get();
    }

    @Override public long getSwapPagesOut() {
        return pagesOut.get();
    }
}
