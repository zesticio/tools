
package com.zestic.system.software.common;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.os.OSThread;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Common methods for OSThread implementation
 */
@ThreadSafe public abstract class AbstractOSThread implements OSThread {

    private final Supplier<Double> cumulativeCpuLoad =
        memoize(this::queryCumulativeCpuLoad, defaultExpiration());

    private final int owningProcessId;

    protected AbstractOSThread(int processId) {
        this.owningProcessId = processId;
    }

    @Override public int getOwningProcessId() {
        return this.owningProcessId;
    }

    @Override public double getThreadCpuLoadCumulative() {
        return cumulativeCpuLoad.get();
    }

    private double queryCumulativeCpuLoad() {
        return getUpTime() > 0d ? (getKernelTime() + getUserTime()) / (double) getUpTime() : 0d;
    }

    @Override public double getThreadCpuLoadBetweenTicks(OSThread priorSnapshot) {
        if (priorSnapshot != null && owningProcessId == priorSnapshot.getOwningProcessId()
            && getThreadId() == priorSnapshot.getThreadId()
            && getUpTime() > priorSnapshot.getUpTime()) {
            return (getUserTime() - priorSnapshot.getUserTime() + getKernelTime()
                - priorSnapshot.getKernelTime()) / (double) (getUpTime()
                - priorSnapshot.getUpTime());
        }
        return getThreadCpuLoadCumulative();
    }

    @Override public String toString() {
        return "OSThread [threadId=" + getThreadId() + ", owningProcessId=" + getOwningProcessId()
            + ", name=" + getName() + ", state=" + getState() + ", kernelTime=" + getKernelTime()
            + ", userTime=" + getUserTime() + ", upTime=" + getUpTime() + ", startTime="
            + getStartTime() + ", startMemoryAddress=0x" + String.format("%x",
            getStartMemoryAddress()) + ", contextSwitches=" + getContextSwitches()
            + ", minorFaults=" + getMinorFaults() + ", majorFaults=" + getMajorFaults() + "]";
    }
}
