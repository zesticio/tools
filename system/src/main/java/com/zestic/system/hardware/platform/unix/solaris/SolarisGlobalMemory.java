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
