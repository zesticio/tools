/*
 * MIT License
 *
 * Copyright (c) 2010 - 2021 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import com.zestic.log.Log;
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
@ThreadSafe final class WindowsVirtualMemory extends AbstractVirtualMemory {

    private static final Log LOG = Log.get();

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
            LOG.error("Failed to get Performance Info. Error code: {}",
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

    @Override public long getSwapUsed() {
        return this.global.getPageSize() * used.get();
    }

    @Override public long getSwapTotal() {
        return this.global.getPageSize() * totalVmaxVused.get().getA();
    }

    @Override public long getVirtualMax() {
        return this.global.getPageSize() * totalVmaxVused.get().getB();
    }

    @Override public long getVirtualInUse() {
        return this.global.getPageSize() * totalVmaxVused.get().getC();
    }

    @Override public long getSwapPagesIn() {
        return swapInOut.get().getA();
    }

    @Override public long getSwapPagesOut() {
        return swapInOut.get().getB();
    }
}
