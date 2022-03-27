
package com.zestic.system.software.os.mac;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractOSThread;
import com.zestic.system.software.os.OSProcess;

/*
 * OSThread implementation
 */
@ThreadSafe public class MacOSThread extends AbstractOSThread {

    private final int threadId;
    private final OSProcess.State state;
    private final long kernelTime;
    private final long userTime;
    private final long startTime;
    private final long upTime;
    private final int priority;

    public MacOSThread(int pid, int threadId, OSProcess.State state, long kernelTime, long userTime,
        long startTime, long upTime, int priority) {
        super(pid);
        this.threadId = threadId;
        this.state = state;
        this.kernelTime = kernelTime;
        this.userTime = userTime;
        this.startTime = startTime;
        this.upTime = upTime;
        this.priority = priority;
    }

    @Override public int getThreadId() {
        return threadId;
    }

    @Override public OSProcess.State getState() {
        return state;
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
        return priority;
    }
}
