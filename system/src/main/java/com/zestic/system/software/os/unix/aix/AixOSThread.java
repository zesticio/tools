
package com.zestic.system.software.os.unix.aix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractOSThread;
import com.zestic.system.software.os.OSProcess;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.List;
import java.util.Map;

/*
 * OSThread implementation
 */
@ThreadSafe public class AixOSThread extends AbstractOSThread {

    private int threadId;
    private OSProcess.State state = OSProcess.State.INVALID;
    private long contextSwitches;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private int priority;

    public AixOSThread(int pid, Map<AixOSProcess.PsThreadColumns, String> threadMap) {
        super(pid);
        updateAttributes(threadMap);
    }

    @Override public int getThreadId() {
        return this.threadId;
    }

    @Override public OSProcess.State getState() {
        return this.state;
    }

    @Override public long getContextSwitches() {
        return this.contextSwitches;
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
        List<String> threadListInfoPs =
            ExecutingCommand.runNative("ps -m -o THREAD -p " + getOwningProcessId());
        // 1st row is header, 2nd row is process data.
        if (threadListInfoPs.size() > 2) {
            threadListInfoPs.remove(0); // header removed
            threadListInfoPs.remove(0); // process data removed
            String tidStr = Integer.toString(this.getThreadId());
            for (String threadInfo : threadListInfoPs) {
                Map<AixOSProcess.PsThreadColumns, String> threadMap =
                    ParseUtil.stringToEnumMap(AixOSProcess.PsThreadColumns.class, threadInfo.trim(),
                        ' ');
                if (threadMap.containsKey(AixOSProcess.PsThreadColumns.COMMAND) && tidStr.equals(
                    threadMap.get(AixOSProcess.PsThreadColumns.TID))) {
                    return updateAttributes(threadMap);
                }
            }
        }
        this.state = OSProcess.State.INVALID;
        return false;
    }

    private boolean updateAttributes(Map<AixOSProcess.PsThreadColumns, String> threadMap) {
        this.threadId =
            ParseUtil.parseIntOrDefault(threadMap.get(AixOSProcess.PsThreadColumns.TID), 0);
        this.state = AixOSProcess.getStateFromOutput(
            threadMap.get(AixOSProcess.PsThreadColumns.ST).charAt(0));
        this.priority =
            ParseUtil.parseIntOrDefault(threadMap.get(AixOSProcess.PsThreadColumns.PRI), 0);
        return true;
    }
}
