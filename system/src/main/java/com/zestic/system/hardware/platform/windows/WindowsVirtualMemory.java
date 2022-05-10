
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.windows.perfmon.MemoryInformation;
import com.zestic.system.driver.windows.perfmon.MemoryInformation.PageSwapProperty;
import com.zestic.system.driver.windows.perfmon.PagingFile;
import com.zestic.system.hardware.common.AbstractVirtualMemory;
import com.zestic.system.util.tuples.Pair;
import com.zestic.system.util.tuples.Triplet;

import java.util.Map;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained from WMI
 */
@ThreadSafe
final class WindowsVirtualMemory extends AbstractVirtualMemory {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WindowsVirtualMemory.class);

    private final WindowsGlobalMemory global;

    private final Supplier<Long> used =
            memoize(WindowsVirtualMemory::querySwapUsed, defaultExpiration());

    private final Supplier<Triplet<Long, Long, Long>> totalVmaxVused =
            memoize(WindowsVirtualMemory::querySwapTotalVirtMaxVirtUsed, defaultExpiration());

    private final Supplier<Pair<Long, Long>> swapInOut =
            memoize(WindowsVirtualMemory::queryPageSwaps, defaultExpiration());

    /*
     * Constructor for WindowsVirtualMemory.
     *
     * @param windowsGlobalMemory The parent global memory class instantiating this
     */
    WindowsVirtualMemory(WindowsGlobalMemory windowsGlobalMemory) {
        this.global = windowsGlobalMemory;
    }

    private static long querySwapUsed() {
        return PagingFile.querySwapUsed()
                .getOrDefault(PagingFile.PagingPercentProperty.PERCENTUSAGE, 0L);
    }

    private static Triplet<Long, Long, Long> querySwapTotalVirtMaxVirtUsed() {
        PERFORMANCE_INFORMATION perfInfo = new PERFORMANCE_INFORMATION();
        if (!Psapi.INSTANCE.GetPerformanceInfo(perfInfo, perfInfo.size())) {
            logger.error("Failed to get Performance Info. Error code: {}" +
                    Kernel32.INSTANCE.GetLastError());
            return new Triplet<>(0L, 0L, 0L);
        }
        return new Triplet<>(perfInfo.CommitLimit.longValue() - perfInfo.PhysicalTotal.longValue(),
                perfInfo.CommitLimit.longValue(), perfInfo.CommitTotal.longValue());
    }

    private static Pair<Long, Long> queryPageSwaps() {
        Map<PageSwapProperty, Long> valueMap = MemoryInformation.queryPageSwaps();
        return new Pair<>(valueMap.getOrDefault(PageSwapProperty.PAGESINPUTPERSEC, 0L),
                valueMap.getOrDefault(PageSwapProperty.PAGESOUTPUTPERSEC, 0L));
    }

    @Override
    public long getSwapUsed() {
        return this.global.getPageSize() * used.get();
    }

    @Override
    public long getSwapTotal() {
        return this.global.getPageSize() * totalVmaxVused.get().getA();
    }

    @Override
    public long getVirtualMax() {
        return this.global.getPageSize() * totalVmaxVused.get().getB();
    }

    @Override
    public long getVirtualInUse() {
        return this.global.getPageSize() * totalVmaxVused.get().getC();
    }

    @Override
    public long getSwapPagesIn() {
        return swapInOut.get().getA();
    }

    @Override
    public long getSwapPagesOut() {
        return swapInOut.get().getB();
    }
}
