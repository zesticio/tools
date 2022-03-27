
package com.zestic.system.software.os.windows;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.windows.registry.ThreadPerformanceData;
import com.zestic.system.driver.windows.registry.ThreadPerformanceData.PerfCounterBlock;
import com.zestic.system.software.common.AbstractOSThread;
import com.zestic.system.software.os.OSProcess;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/*
 * OSThread implementation
 */
@ThreadSafe public class WindowsOSThread extends AbstractOSThread {

    private final int threadId;
    private String name;
    private OSProcess.State state;
    private long startMemoryAddress;
    private long contextSwitches;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private int priority;

    public WindowsOSThread(int pid, int tid, String procName, PerfCounterBlock pcb) {
        super(pid);
        this.threadId = tid;
        updateAttributes(procName, pcb);
    }

    @Override public int getThreadId() {
        return threadId;
    }

    @Override public String getName() {
        return name;
    }

    @Override public OSProcess.State getState() {
        return state;
    }

    @Override public long getStartMemoryAddress() {
        return startMemoryAddress;
    }

    @Override public long getContextSwitches() {
        return contextSwitches;
    }

    @Override public long getKernelTime() {
        return kernelTime;
    }

    @Override public long getUserTime() {
        return userTime;
    }

    @Override public long getStartTime() {
        return startTime;
    }

    @Override public long getUpTime() {
        return upTime;
    }

    @Override public int getPriority() {
        return this.priority;
    }

    @Override public boolean updateAttributes() {
        Set<Integer> pids = Collections.singleton(getOwningProcessId());
        // Get data from the registry if possible
        Map<Integer, PerfCounterBlock> threads =
            ThreadPerformanceData.buildThreadMapFromRegistry(pids);
        // otherwise performance counters with WMI backup
        if (threads == null) {
            threads = ThreadPerformanceData.buildThreadMapFromPerfCounters(pids);
        }
        return updateAttributes(this.name.split("/")[0], threads.get(getThreadId()));
    }

    private boolean updateAttributes(String procName, PerfCounterBlock pcb) {
        if (pcb == null) {
            this.state = OSProcess.State.INVALID;
            return false;
        } else if (pcb.getName().contains("/") || procName.isEmpty()) {
            name = pcb.getName();
        } else {
            this.name = procName + "/" + pcb.getName();
        }
        if (pcb.getThreadWaitReason() == 5) {
            state = OSProcess.State.SUSPENDED;
        } else {
            switch (pcb.getThreadState()) {
                case 0:
                    state = OSProcess.State.NEW;
                    break;
                case 2:
                case 3:
                    state = OSProcess.State.RUNNING;
                    break;
                case 4:
                    state = OSProcess.State.STOPPED;
                    break;
                case 5:
                    state = OSProcess.State.SLEEPING;
                    break;
                case 1:
                case 6:
                    state = OSProcess.State.WAITING;
                    break;
                case 7:
                default:
                    state = OSProcess.State.OTHER;
            }
        }
        startMemoryAddress = pcb.getStartAddress();
        contextSwitches = pcb.getContextSwitches();
        kernelTime = pcb.getKernelTime();
        userTime = pcb.getUserTime();
        startTime = pcb.getStartTime();
        upTime = System.currentTimeMillis() - pcb.getStartTime();
        priority = pcb.getPriority();
        return true;
    }
}
