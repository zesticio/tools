
package com.zestic.system.software.os.unix.freebsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractOSThread;
import com.zestic.system.software.os.OSProcess;
import com.zestic.system.software.os.unix.freebsd.FreeBsdOSProcess.PsThreadColumns;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.List;
import java.util.Map;

/*
 * OSThread implementation
 */
@ThreadSafe public class FreeBsdOSThread extends AbstractOSThread {

    private int threadId;
    private String name = "";
    private OSProcess.State state = OSProcess.State.INVALID;
    private long minorFaults;
    private long majorFaults;
    private long startMemoryAddress;
    private long contextSwitches;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private int priority;

    public FreeBsdOSThread(int processId, Map<PsThreadColumns, String> threadMap) {
        super(processId);
        updateAttributes(threadMap);
    }

    @Override public int getThreadId() {
        return this.threadId;
    }

    @Override public String getName() {
        return this.name;
    }

    @Override public OSProcess.State getState() {
        return this.state;
    }

    @Override public long getStartMemoryAddress() {
        return this.startMemoryAddress;
    }

    @Override public long getContextSwitches() {
        return this.contextSwitches;
    }

    @Override public long getMinorFaults() {
        return this.minorFaults;
    }

    @Override public long getMajorFaults() {
        return this.majorFaults;
    }

    @Override public long getKernelTime() {
        return this.kernelTime;
    }

    @Override public long getUserTime() {
        return this.userTime;
    }

    @Override public long getUpTime() {
        return this.upTime;
    }

    @Override public long getStartTime() {
        return this.startTime;
    }

    @Override public int getPriority() {
        return this.priority;
    }

    @Override public boolean updateAttributes() {
        List<String> threadList = ExecutingCommand.runNative(
            "ps -awwxo " + FreeBsdOSProcess.PS_THREAD_COLUMNS + " -H -p " + getOwningProcessId());
        // there is no switch for thread in ps command, hence filtering.
        String lwpStr = Integer.toString(this.threadId);
        for (String psOutput : threadList) {
            Map<PsThreadColumns, String> threadMap =
                ParseUtil.stringToEnumMap(PsThreadColumns.class, psOutput.trim(), ' ');
            if (threadMap.containsKey(PsThreadColumns.PRI) && lwpStr.equals(
                threadMap.get(PsThreadColumns.LWP))) {
                return updateAttributes(threadMap);
            }
        }
        this.state = OSProcess.State.INVALID;
        return false;
    }

    private boolean updateAttributes(Map<PsThreadColumns, String> threadMap) {
        this.name = threadMap.get(PsThreadColumns.TDNAME);
        this.threadId = ParseUtil.parseIntOrDefault(threadMap.get(PsThreadColumns.LWP), 0);
        switch (threadMap.get(PsThreadColumns.STATE).charAt(0)) {
            case 'R':
                this.state = OSProcess.State.RUNNING;
                break;
            case 'I':
            case 'S':
                this.state = OSProcess.State.SLEEPING;
                break;
            case 'D':
            case 'L':
            case 'U':
                this.state = OSProcess.State.WAITING;
                break;
            case 'Z':
                this.state = OSProcess.State.ZOMBIE;
                break;
            case 'T':
                this.state = OSProcess.State.STOPPED;
                break;
            default:
                this.state = OSProcess.State.OTHER;
                break;
        }
        long elapsedTime = ParseUtil.parseDHMSOrDefault(threadMap.get(PsThreadColumns.ETIMES), 0L);
        // Avoid divide by zero for processes up less than a second
        this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
        long now = System.currentTimeMillis();
        this.startTime = now - this.upTime;
        this.kernelTime = ParseUtil.parseDHMSOrDefault(threadMap.get(PsThreadColumns.SYSTIME), 0L);
        this.userTime =
            ParseUtil.parseDHMSOrDefault(threadMap.get(PsThreadColumns.TIME), 0L) - this.kernelTime;
        this.startMemoryAddress =
            ParseUtil.hexStringToLong(threadMap.get(PsThreadColumns.TDADDR), 0L);
        long nonVoluntaryContextSwitches =
            ParseUtil.parseLongOrDefault(threadMap.get(PsThreadColumns.NIVCSW), 0L);
        long voluntaryContextSwitches =
            ParseUtil.parseLongOrDefault(threadMap.get(PsThreadColumns.NVCSW), 0L);
        this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
        this.majorFaults = ParseUtil.parseLongOrDefault(threadMap.get(PsThreadColumns.MAJFLT), 0L);
        this.minorFaults = ParseUtil.parseLongOrDefault(threadMap.get(PsThreadColumns.MINFLT), 0L);
        this.priority = ParseUtil.parseIntOrDefault(threadMap.get(PsThreadColumns.PRI), 0);
        return true;
    }
}
