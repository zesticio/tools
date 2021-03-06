
package com.zestic.system.software.os.unix.aix;

import com.sun.jna.platform.unix.aix.Perfstat.perfstat_process_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.aix.PsInfo;
import com.zestic.system.driver.unix.aix.perfstat.PerfstatCpu;
import com.zestic.system.software.common.AbstractOSProcess;
import com.zestic.system.software.os.OSProcess;
import com.zestic.system.software.os.OSThread;
import com.zestic.system.software.os.unix.aix.AixOperatingSystem.PsKeywords;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.LsofUtil;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.tuples.Pair;

import java.util.*;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * OSProcess implementation
 */
@ThreadSafe public class AixOSProcess extends AbstractOSProcess {
    private final Supplier<Long> affinityMask =
        memoize(PerfstatCpu::queryCpuAffinityMask, defaultExpiration());
    private Supplier<Integer> bitness = memoize(this::queryBitness);
    private Supplier<Pair<List<String>, Map<String, String>>> cmdEnv =
        memoize(this::queryCommandlineEnvironment);
    private String name;
    private String path = "";
    private String commandLineBackup;
    private Supplier<String> commandLine = memoize(this::queryCommandLine);
    private String user;
    private String userID;
    private String group;
    private String groupID;
    private OSProcess.State state = OSProcess.State.INVALID;
    private int parentProcessID;
    private int threadCount;
    private int priority;
    private long virtualSize;
    private long residentSetSize;
    private long kernelTime;
    private long userTime;
    private long startTime;
    private long upTime;
    private long bytesRead;
    private long bytesWritten;
    private long majorFaults;
    // Memoized copy from OperatingSystem
    private Supplier<perfstat_process_t[]> procCpu;

    public AixOSProcess(int pid, Map<PsKeywords, String> psMap,
        Map<Integer, Pair<Long, Long>> cpuMap, Supplier<perfstat_process_t[]> procCpu) {
        super(pid);
        this.procCpu = procCpu;
        updateAttributes(psMap, cpuMap);
    }

    /**
     * Returns Enum STATE for the state value obtained from status string of
     * thread/process.
     *
     * @param stateValue
     *            state value from the status string
     * @return The state
     */
    static OSProcess.State getStateFromOutput(char stateValue) {
        OSProcess.State state;
        switch (stateValue) {
            case 'O':
                state = OSProcess.State.INVALID;
                break;
            case 'R':
            case 'A':
                state = OSProcess.State.RUNNING;
                break;
            case 'I':
                state = OSProcess.State.WAITING;
                break;
            case 'S':
            case 'W':
                state = OSProcess.State.SLEEPING;
                break;
            case 'Z':
                state = OSProcess.State.ZOMBIE;
                break;
            case 'T':
                state = OSProcess.State.STOPPED;
                break;
            default:
                state = OSProcess.State.OTHER;
                break;
        }
        return state;
    }

    @Override public String getName() {
        return this.name;
    }

    @Override public String getPath() {
        return this.path;
    }

    @Override public String getCommandLine() {
        return this.commandLine.get();
    }

    private String queryCommandLine() {
        String cl = String.join(" ", getArguments());
        return cl.isEmpty() ? this.commandLineBackup : cl;
    }

    @Override public List<String> getArguments() {
        return cmdEnv.get().getA();
    }

    @Override public Map<String, String> getEnvironmentVariables() {
        return cmdEnv.get().getB();
    }

    private Pair<List<String>, Map<String, String>> queryCommandlineEnvironment() {
        return PsInfo.queryArgsEnv(getProcessID());
    }

    @Override public String getCurrentWorkingDirectory() {
        return LsofUtil.getCwd(getProcessID());
    }

    @Override public String getUser() {
        return this.user;
    }

    @Override public String getUserID() {
        return this.userID;
    }

    @Override public String getGroup() {
        return this.group;
    }

    @Override public String getGroupID() {
        return this.groupID;
    }

    @Override public OSProcess.State getState() {
        return this.state;
    }

    @Override public int getParentProcessID() {
        return this.parentProcessID;
    }

    @Override public int getThreadCount() {
        return this.threadCount;
    }

    @Override public int getPriority() {
        return this.priority;
    }

    @Override public long getVirtualSize() {
        return this.virtualSize;
    }

    @Override public long getResidentSetSize() {
        return this.residentSetSize;
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

    @Override public long getBytesRead() {
        return this.bytesRead;
    }

    @Override public long getBytesWritten() {
        return this.bytesWritten;
    }

    @Override public long getOpenFiles() {
        return LsofUtil.getOpenFiles(getProcessID());
    }

    @Override public int getBitness() {
        return this.bitness.get();
    }

    private int queryBitness() {
        List<String> pflags = ExecutingCommand.runNative("pflags " + getProcessID());
        for (String line : pflags) {
            if (line.contains("data model")) {
                if (line.contains("LP32")) {
                    return 32;
                } else if (line.contains("LP64")) {
                    return 64;
                }
            }
        }
        return 0;
    }

    @Override public long getAffinityMask() {
        // Need to capture BND field from ps
        // ps -m -o THREAD -p 12345
        // BND field for PID is either a dash (all processors) or the processor it's
        // bound to, do 1L << # to get mask
        long mask = 0L;
        List<String> processAffinityInfoList =
            ExecutingCommand.runNative("ps -m -o THREAD -p " + getProcessID());
        if (processAffinityInfoList.size() > 2) { // what happens when the process has not thread?
            processAffinityInfoList.remove(0); // remove header row
            processAffinityInfoList.remove(0); // remove process row
            for (String processAffinityInfo : processAffinityInfoList) { // affinity information is in thread row
                Map<PsThreadColumns, String> threadMap =
                    ParseUtil.stringToEnumMap(PsThreadColumns.class, processAffinityInfo.trim(),
                        ' ');
                if (threadMap.containsKey(PsThreadColumns.COMMAND)
                    && threadMap.get(PsThreadColumns.ST).charAt(0)
                    != 'Z') { // only non-zombie threads
                    String bnd = threadMap.get(PsThreadColumns.BND);
                    if (bnd.charAt(0) == '-') { // affinity to all processors
                        return this.affinityMask.get();
                    } else {
                        int affinity = ParseUtil.parseIntOrDefault(bnd, 0);
                        mask |= 1L << affinity;
                    }
                }
            }
        }
        return mask;
    }

    @Override public List<OSThread> getThreadDetails() {
        List<String> threadListInfoPs =
            ExecutingCommand.runNative("ps -m -o THREAD -p " + getProcessID());
        // 1st row is header, 2nd row is process data.
        if (threadListInfoPs.size() > 2) {
            List<OSThread> threads = new ArrayList<>();
            threadListInfoPs.remove(0); // header removed
            threadListInfoPs.remove(0); // process data removed
            for (String threadInfo : threadListInfoPs) {
                Map<PsThreadColumns, String> threadMap =
                    ParseUtil.stringToEnumMap(PsThreadColumns.class, threadInfo.trim(), ' ');
                if (threadMap.containsKey(PsThreadColumns.COMMAND)) {
                    threads.add(new AixOSThread(getProcessID(), threadMap));
                }
            }
            return threads;
        }
        return Collections.emptyList();
    }

    @Override public long getMajorFaults() {
        return this.majorFaults;
    }

    @Override public boolean updateAttributes() {
        perfstat_process_t[] perfstat = procCpu.get();
        List<String> procList = ExecutingCommand.runNative(
            "ps -o " + AixOperatingSystem.PS_COMMAND_ARGS + " -p " + getProcessID());
        // Parse array to map of user/system times
        Map<Integer, Pair<Long, Long>> cpuMap = new HashMap<>();
        for (perfstat_process_t stat : perfstat) {
            cpuMap.put((int) stat.pid, new Pair<>((long) stat.ucpu_time, (long) stat.scpu_time));
        }
        if (procList.size() > 1) {
            Map<PsKeywords, String> psMap =
                ParseUtil.stringToEnumMap(PsKeywords.class, procList.get(1).trim(), ' ');
            // Check if last (thus all) value populated
            if (psMap.containsKey(PsKeywords.ARGS)) {
                return updateAttributes(psMap, cpuMap);
            }
        }
        this.state = OSProcess.State.INVALID;
        return false;
    }

    private boolean updateAttributes(Map<PsKeywords, String> psMap,
        Map<Integer, Pair<Long, Long>> cpuMap) {
        long now = System.currentTimeMillis();
        this.state = getStateFromOutput(psMap.get(PsKeywords.ST).charAt(0));
        this.parentProcessID = ParseUtil.parseIntOrDefault(psMap.get(PsKeywords.PPID), 0);
        this.user = psMap.get(PsKeywords.USER);
        this.userID = psMap.get(PsKeywords.UID);
        this.group = psMap.get(PsKeywords.GROUP);
        this.groupID = psMap.get(PsKeywords.GID);
        this.threadCount = ParseUtil.parseIntOrDefault(psMap.get(PsKeywords.THCOUNT), 0);
        this.priority = ParseUtil.parseIntOrDefault(psMap.get(PsKeywords.PRI), 0);
        // These are in KB, multiply
        this.virtualSize = ParseUtil.parseLongOrDefault(psMap.get(PsKeywords.VSIZE), 0) << 10;
        this.residentSetSize = ParseUtil.parseLongOrDefault(psMap.get(PsKeywords.RSSIZE), 0) << 10;
        long elapsedTime = ParseUtil.parseDHMSOrDefault(psMap.get(PsKeywords.ETIME), 0L);
        if (cpuMap.containsKey(getProcessID())) {
            Pair<Long, Long> userSystem = cpuMap.get(getProcessID());
            this.userTime = userSystem.getA();
            this.kernelTime = userSystem.getB();
        } else {
            this.userTime = ParseUtil.parseDHMSOrDefault(psMap.get(PsKeywords.TIME), 0L);
            this.kernelTime = 0L;
        }
        // Avoid divide by zero for processes up less than a second
        this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
        while (this.upTime < this.userTime + this.kernelTime) {
            this.upTime += 500L;
        }
        this.startTime = now - this.upTime;
        this.name = psMap.get(PsKeywords.COMM);
        this.majorFaults = ParseUtil.parseLongOrDefault(psMap.get(PsKeywords.PAGEIN), 0L);
        this.commandLineBackup = psMap.get(PsKeywords.ARGS);
        this.path = ParseUtil.whitespaces.split(this.commandLineBackup)[0];
        return true;
    }

    /*
     * Package-private for use by AIXOSThread
     */
    enum PsThreadColumns {
        USER, PID, PPID, TID, ST, CP, PRI, SC, WCHAN, F, TT, BND, COMMAND;
    }
}
