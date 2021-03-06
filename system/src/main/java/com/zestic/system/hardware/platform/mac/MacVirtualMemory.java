
package com.zestic.system.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.SystemB.VMStatistics;
import com.sun.jna.platform.mac.SystemB.XswUsage;
import com.sun.jna.ptr.IntByReference;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractVirtualMemory;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.mac.SysctlUtil;
import com.zestic.system.util.tuples.Pair;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by host_statistics (vm_stat) and sysctl.
 */
@ThreadSafe
final class MacVirtualMemory extends AbstractVirtualMemory {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);

    private final MacGlobalMemory global;

    private final Supplier<Pair<Long, Long>> usedTotal =
            memoize(MacVirtualMemory::querySwapUsage, defaultExpiration());

    private final Supplier<Pair<Long, Long>> inOut =
            memoize(MacVirtualMemory::queryVmStat, defaultExpiration());

    /*
     * Constructor for MacVirtualMemory.
     *
     * @param macGlobalMemory The parent global memory class instantiating this
     */
    MacVirtualMemory(MacGlobalMemory macGlobalMemory) {
        this.global = macGlobalMemory;
    }

    private static Pair<Long, Long> querySwapUsage() {
        long swapUsed = 0L;
        long swapTotal = 0L;
        XswUsage xswUsage = new XswUsage();
        if (SysctlUtil.sysctl("vm.swapusage", xswUsage)) {
            swapUsed = xswUsage.xsu_used;
            swapTotal = xswUsage.xsu_total;
        }
        return new Pair<>(swapUsed, swapTotal);
    }

    private static Pair<Long, Long> queryVmStat() {
        long swapPagesIn = 0L;
        long swapPagesOut = 0L;
        VMStatistics vmStats = new VMStatistics();
        if (0 == SystemB.INSTANCE.host_statistics(SystemB.INSTANCE.mach_host_self(),
                SystemB.HOST_VM_INFO, vmStats, new IntByReference(vmStats.size() / SystemB.INT_SIZE))) {
            swapPagesIn = ParseUtil.unsignedIntToLong(vmStats.pageins);
            swapPagesOut = ParseUtil.unsignedIntToLong(vmStats.pageouts);
        } else {
            LOG.error("Failed to get host VM info. Error code: {" + Native.getLastError() + "}");
        }
        return new Pair<>(swapPagesIn, swapPagesOut);
    }

    @Override
    public long getSwapUsed() {
        return usedTotal.get().getA();
    }

    @Override
    public long getSwapTotal() {
        return usedTotal.get().getB();
    }

    @Override
    public long getVirtualMax() {
        return this.global.getTotal() + getSwapTotal();
    }

    @Override
    public long getVirtualInUse() {
        return this.global.getTotal() - this.global.getAvailable() + getSwapUsed();
    }

    @Override
    public long getSwapPagesIn() {
        return inOut.get().getA();
    }

    @Override
    public long getSwapPagesOut() {
        return inOut.get().getB();
    }
}
