
package com.zestic.system.hardware.platform.linux;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractVirtualMemory;
import com.zestic.system.util.FileUtil;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.linux.ProcPath;
import com.zestic.system.util.tuples.Pair;
import com.zestic.system.util.tuples.Triplet;

import java.util.List;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by /proc/meminfo and /proc/vmstat
 */
@ThreadSafe final class LinuxVirtualMemory extends AbstractVirtualMemory {

    private final LinuxGlobalMemory global;

    private final Supplier<Triplet<Long, Long, Long>> usedTotalCommitLim =
        memoize(LinuxVirtualMemory::queryMemInfo, defaultExpiration());

    private final Supplier<Pair<Long, Long>> inOut =
        memoize(LinuxVirtualMemory::queryVmStat, defaultExpiration());

    /*
     * Constructor for LinuxVirtualMemory.
     *
     * @param linuxGlobalMemory The parent global memory class instantiating this
     */
    LinuxVirtualMemory(LinuxGlobalMemory linuxGlobalMemory) {
        this.global = linuxGlobalMemory;
    }

    private static Triplet<Long, Long, Long> queryMemInfo() {
        long swapFree = 0L;
        long swapTotal = 0L;
        long commitLimit = 0L;

        List<String> procMemInfo = FileUtil.readFile(ProcPath.MEMINFO);
        for (String checkLine : procMemInfo) {
            String[] memorySplit = ParseUtil.whitespaces.split(checkLine);
            if (memorySplit.length > 1) {
                switch (memorySplit[0]) {
                    case "SwapTotal:":
                        swapTotal = parseMeminfo(memorySplit);
                        break;
                    case "SwapFree:":
                        swapFree = parseMeminfo(memorySplit);
                        break;
                    case "CommitLimit:":
                        commitLimit = parseMeminfo(memorySplit);
                        break;
                    default:
                        // do nothing with other lines
                        break;
                }
            }
        }
        return new Triplet<>(swapTotal - swapFree, swapTotal, commitLimit);
    }

    private static Pair<Long, Long> queryVmStat() {
        long swapPagesIn = 0L;
        long swapPagesOut = 0L;
        List<String> procVmStat = FileUtil.readFile(ProcPath.VMSTAT);
        for (String checkLine : procVmStat) {
            String[] memorySplit = ParseUtil.whitespaces.split(checkLine);
            if (memorySplit.length > 1) {
                switch (memorySplit[0]) {
                    case "pswpin":
                        swapPagesIn = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
                        break;
                    case "pswpout":
                        swapPagesOut = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
                        break;
                    default:
                        // do nothing with other lines
                        break;
                }
            }
        }
        return new Pair<>(swapPagesIn, swapPagesOut);
    }

    /*
     * Parses lines from the display of /proc/meminfo
     *
     * @param memorySplit Array of Strings representing the 3 columns of /proc/meminfo
     * @return value, multiplied by 1024 if kB is specified
     */
    private static long parseMeminfo(String[] memorySplit) {
        if (memorySplit.length < 2) {
            return 0L;
        }
        long memory = ParseUtil.parseLongOrDefault(memorySplit[1], 0L);
        if (memorySplit.length > 2 && "kB".equals(memorySplit[2])) {
            memory *= 1024;
        }
        return memory;
    }

    @Override public long getSwapUsed() {
        return usedTotalCommitLim.get().getA();
    }

    @Override public long getSwapTotal() {
        return usedTotalCommitLim.get().getB();
    }

    @Override public long getVirtualMax() {
        return usedTotalCommitLim.get().getC();
    }

    @Override public long getVirtualInUse() {
        return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
    }

    @Override public long getSwapPagesIn() {
        return inOut.get().getA();
    }

    @Override public long getSwapPagesOut() {
        return inOut.get().getB();
    }
}
