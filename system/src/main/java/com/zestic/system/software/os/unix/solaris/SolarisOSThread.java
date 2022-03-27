
package com.zestic.system.software.os.unix.solaris;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractOSThread;
import com.zestic.system.software.os.OSProcess;
import com.zestic.system.software.os.unix.solaris.SolarisOSProcess.PsThreadColumns;
import com.zestic.system.software.os.unix.solaris.SolarisOperatingSystem.PrstatKeywords;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.List;
import java.util.Map;

/*
 * OSThread implementation
 */
@ThreadSafe public class SolarisOSThread extends AbstractOSThread {

    private int threadId;
    private OSProcess.State state = OSProcess.State.INVALID;
    private long startMemoryAddress;
    private long contextSwitches;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private int priority;

    public SolarisOSThread(int pid, Map<PsThreadColumns, String> psMap,
        Map<PrstatKeywords, String> prstatMap) {
        super(pid);
        updateAttributes(psMap, prstatMap);
    }

    @Override public int getThreadId() {
        return this.threadId;
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
        int pid = getOwningProcessId();
        List<String> threadList = ExecutingCommand.runNative(
            "ps -o " + SolarisOSProcess.PS_THREAD_COLUMNS + " -p " + pid);
        if (threadList.size() > 1) {
            // there is no switch for thread in ps command, hence filtering.
            String lwpStr = Integer.toString(this.threadId);
            for (String psOutput : threadList) {
                Map<PsThreadColumns, String> threadMap =
                    ParseUtil.stringToEnumMap(PsThreadColumns.class, psOutput.trim(), ' ');
                if (threadMap.containsKey(PsThreadColumns.PRI) && lwpStr.equals(
                    threadMap.get(PsThreadColumns.LWP))) {
                    List<String> prstatList =
                        ExecutingCommand.runNative("prstat -L -v -p " + pid + " 1 1");
                    String prstatRow = "";
                    for (String s : prstatList) {
                        String row = s.trim();
                        if (row.endsWith("/" + lwpStr)) {
                            prstatRow = row;
                            break;
                        }
                    }
                    Map<PrstatKeywords, String> prstatMap =
                        ParseUtil.stringToEnumMap(PrstatKeywords.class, prstatRow, ' ');
                    return updateAttributes(threadMap, prstatMap);
                }
            }
        }
        this.state = OSProcess.State.INVALID;
        return false;
    }

    private boolean updateAttributes(Map<PsThreadColumns, String> psMap,
        Map<PrstatKeywords, String> prstatMap) {
        this.threadId = ParseUtil.parseIntOrDefault(psMap.get(PsThreadColumns.LWP), 0);
        this.state = SolarisOSProcess.getStateFromOutput(psMap.get(PsThreadColumns.S).charAt(0));
        // Avoid divide by zero for processes up less than a second
        long elapsedTime = ParseUtil.parseDHMSOrDefault(psMap.get(PsThreadColumns.ETIME), 0L);
        this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
        long now = System.currentTimeMillis();
        this.startTime = now - this.upTime;
        this.kernelTime = 0L;
        this.userTime = ParseUtil.parseDHMSOrDefault(psMap.get(PsThreadColumns.TIME), 0L);
        this.startMemoryAddress = ParseUtil.hexStringToLong(psMap.get(PsThreadColumns.ADDR), 0L);
        this.priority = ParseUtil.parseIntOrDefault(psMap.get(PsThreadColumns.PRI), 0);
        long nonVoluntaryContextSwitches =
            ParseUtil.parseLongOrDefault(prstatMap.get(PrstatKeywords.ICX), 0L);
        long voluntaryContextSwitches =
            ParseUtil.parseLongOrDefault(prstatMap.get(PrstatKeywords.VCX), 0L);
        this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
        return true;
    }
}
