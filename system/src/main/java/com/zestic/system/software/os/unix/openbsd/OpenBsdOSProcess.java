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
package com.zestic.system.software.os.unix.openbsd;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import com.zestic.log.Log;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.jna.platform.unix.openbsd.OpenBsdLibc;
import com.zestic.system.software.common.AbstractOSProcess;
import com.zestic.system.software.os.OSProcess;
import com.zestic.system.software.os.OSThread;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.unix.openbsd.FstatUtil;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * OSProcess implementation
 */
@ThreadSafe public class OpenBsdOSProcess extends AbstractOSProcess {

    static final String PS_THREAD_COLUMNS =
        Arrays.stream(PsThreadColumns.values()).map(Enum::name).map(String::toLowerCase)
            .collect(Collectors.joining(","));
    private static final Log LOG = Log.get();
    private static final int ARGMAX;

    static {
        int[] mib = new int[2];
        mib[0] = 1; // CTL_KERN
        mib[1] = 8; // KERN_ARGMAX
        Memory m = new Memory(Integer.BYTES);
        size_t.ByReference size = new size_t.ByReference(new size_t(Integer.BYTES));
        if (OpenBsdLibc.INSTANCE.sysctl(mib, mib.length, m, size, null, size_t.ZERO) == 0) {
            ARGMAX = m.getInt(0);
        } else {
            LOG.warn(
                "Failed sysctl call for process arguments max size (kern.argmax). Error code: {}",
                Native.getLastError());
            ARGMAX = 0;
        }
    }

    private Supplier<List<String>> arguments = memoize(this::queryArguments);
    private Supplier<Map<String, String>> environmentVariables =
        memoize(this::queryEnvironmentVariables);
    private String name;
    private String path = "";
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
    private long minorFaults;
    private long majorFaults;
    private long contextSwitches;
    private int bitness;
    private String commandLineBackup;
    private Supplier<String> commandLine = memoize(this::queryCommandLine);
    public OpenBsdOSProcess(int pid, Map<OpenBsdOperatingSystem.PsKeywords, String> psMap) {
        super(pid);
        // OpenBSD does not maintain a compatibility layer.
        // Process bitness is OS bitness
        this.bitness = Native.LONG_SIZE * 8;
        updateThreadCount();
        updateAttributes(psMap);
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
        return arguments.get();
    }

    private List<String> queryArguments() {
        if (ARGMAX > 0) {
            // Get arguments via sysctl(3)
            int[] mib = new int[4];
            mib[0] = 1; // CTL_KERN
            mib[1] = 55; // KERN_PROC_ARGS
            mib[2] = getProcessID();
            mib[3] = 1; // KERN_PROC_ARGV
            // Allocate memory for arguments
            Memory m = new Memory(ARGMAX);
            size_t.ByReference size = new size_t.ByReference(new size_t(ARGMAX));
            // Fetch arguments
            if (OpenBsdLibc.INSTANCE.sysctl(mib, mib.length, m, size, null, size_t.ZERO) == 0) {
                // Returns a null-terminated list of pointers to the actual data
                List<String> args = new ArrayList<>();
                // To iterate the pointer-list
                long offset = 0;
                // Get the data base address to calculate offsets
                long baseAddr = Pointer.nativeValue(m);
                long maxAddr = baseAddr + size.getValue().longValue();
                // Get the address of the data. If null (0) we're done iterating
                long argAddr = Pointer.nativeValue(m.getPointer(offset));
                while (argAddr > baseAddr && argAddr < maxAddr) {
                    args.add(m.getString(argAddr - baseAddr));
                    offset += Native.POINTER_SIZE;
                    argAddr = Pointer.nativeValue(m.getPointer(offset));
                }
                return Collections.unmodifiableList(args);
            }
        }
        return Collections.emptyList();
    }

    @Override public Map<String, String> getEnvironmentVariables() {
        return environmentVariables.get();
    }

    private Map<String, String> queryEnvironmentVariables() {
        // Get environment variables via sysctl(3)
        int[] mib = new int[4];
        mib[0] = 1; // CTL_KERN
        mib[1] = 55; // KERN_PROC_ARGS
        mib[2] = getProcessID();
        mib[3] = 3; // KERN_PROC_ENV
        // Allocate memory for environment variables
        Memory m = new Memory(ARGMAX);
        size_t.ByReference size = new size_t.ByReference(new size_t(ARGMAX));
        // Fetch environment variables
        if (OpenBsdLibc.INSTANCE.sysctl(mib, mib.length, m, size, null, size_t.ZERO) == 0) {
            // Returns a null-terminated list of pointers to the actual data
            Map<String, String> env = new LinkedHashMap<>();
            // To iterate the pointer-list
            long offset = 0;
            // Get the data base address to calculate offsets
            long baseAddr = Pointer.nativeValue(m);
            long maxAddr = baseAddr + size.longValue();
            // Get the address of the data. If null (0) we're done iterating
            long argAddr = Pointer.nativeValue(m.getPointer(offset));
            while (argAddr > baseAddr && argAddr < maxAddr) {
                String envStr = m.getString(argAddr - baseAddr);
                int idx = envStr.indexOf('=');
                if (idx > 0) {
                    env.put(envStr.substring(0, idx), envStr.substring(idx + 1));
                }
                offset += Native.POINTER_SIZE;
                argAddr = Pointer.nativeValue(m.getPointer(offset));
            }
            return Collections.unmodifiableMap(env);
        }
        return Collections.emptyMap();
    }

    @Override public String getCurrentWorkingDirectory() {
        return FstatUtil.getCwd(getProcessID());
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
        return FstatUtil.getOpenFiles(getProcessID());
    }

    @Override public int getBitness() {
        return this.bitness;
    }

    @Override public long getAffinityMask() {
        long bitMask = 0L;
        // Would prefer to use native cpuset_getaffinity call but variable sizing is
        // kernel-dependent and requires C macros, so we use commandline instead.
        String cpuset = ExecutingCommand.getFirstAnswer("cpuset -gp " + getProcessID());
        // Sample output:
        // pid 8 mask: 0, 1
        // cpuset: getaffinity: No such process
        String[] split = cpuset.split(":");
        if (split.length > 1) {
            String[] bits = split[1].split(",");
            for (String bit : bits) {
                int bitToSet = ParseUtil.parseIntOrDefault(bit.trim(), -1);
                if (bitToSet >= 0) {
                    bitMask |= 1L << bitToSet;
                }
            }
        }
        return bitMask;
    }

    @Override public List<OSThread> getThreadDetails() {
        List<OSThread> threads = new ArrayList<>();
        String psCommand = "ps -aHwwxo " + PS_THREAD_COLUMNS;
        if (getProcessID() >= 0) {
            psCommand += " -p " + getProcessID();
        }
        List<String> threadList = ExecutingCommand.runNative(psCommand);
        if (threadList.isEmpty() || threadList.size() < 2) {
            return threads;
        }
        // remove header row
        threadList.remove(0);
        // Fill list
        for (String thread : threadList) {
            Map<PsThreadColumns, String> threadMap =
                ParseUtil.stringToEnumMap(PsThreadColumns.class, thread.trim(), ' ');
            if (threadMap.containsKey(PsThreadColumns.ARGS)) {
                threads.add(new OpenBsdOSThread(getProcessID(), threadMap));
            }
        }
        return threads;
    }

    @Override public long getMinorFaults() {
        return this.minorFaults;
    }

    @Override public long getMajorFaults() {
        return this.majorFaults;
    }

    @Override public long getContextSwitches() {
        return this.contextSwitches;
    }

    @Override public boolean updateAttributes() {
        // 'ps' does not provide threadCount or kernelTime on OpenBSD
        String psCommand =
            "ps -awwxo " + OpenBsdOperatingSystem.PS_COMMAND_ARGS + " -p " + getProcessID();
        List<String> procList = ExecutingCommand.runNative(psCommand);
        if (procList.size() > 1) {
            // skip header row
            Map<OpenBsdOperatingSystem.PsKeywords, String> psMap =
                ParseUtil.stringToEnumMap(OpenBsdOperatingSystem.PsKeywords.class,
                    procList.get(1).trim(), ' ');
            // Check if last (thus all) value populated
            if (psMap.containsKey(OpenBsdOperatingSystem.PsKeywords.ARGS)) {
                updateThreadCount();
                return updateAttributes(psMap);
            }
        }
        this.state = OSProcess.State.INVALID;
        return false;
    }

    private boolean updateAttributes(Map<OpenBsdOperatingSystem.PsKeywords, String> psMap) {
        long now = System.currentTimeMillis();
        switch (psMap.get(OpenBsdOperatingSystem.PsKeywords.STATE).charAt(0)) {
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
        this.parentProcessID =
            ParseUtil.parseIntOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.PPID), 0);
        this.user = psMap.get(OpenBsdOperatingSystem.PsKeywords.USER);
        this.userID = psMap.get(OpenBsdOperatingSystem.PsKeywords.UID);
        this.group = psMap.get(OpenBsdOperatingSystem.PsKeywords.GROUP);
        this.groupID = psMap.get(OpenBsdOperatingSystem.PsKeywords.GID);
        this.priority =
            ParseUtil.parseIntOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.PRI), 0);
        // These are in KB, multiply
        this.virtualSize =
            ParseUtil.parseLongOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.VSZ), 0)
                * 1024;
        this.residentSetSize =
            ParseUtil.parseLongOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.RSS), 0)
                * 1024;
        // Avoid divide by zero for processes up less than a second
        long elapsedTime =
            ParseUtil.parseDHMSOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.ETIME), 0L);
        this.upTime = elapsedTime < 1L ? 1L : elapsedTime;
        this.startTime = now - this.upTime;
        this.userTime =
            ParseUtil.parseDHMSOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.CPUTIME), 0L);
        // kernel time is included in user time
        this.kernelTime = 0L;
        this.path = psMap.get(OpenBsdOperatingSystem.PsKeywords.COMM);
        this.name = this.path.substring(this.path.lastIndexOf('/') + 1);
        this.minorFaults =
            ParseUtil.parseLongOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.MINFLT), 0L);
        this.majorFaults =
            ParseUtil.parseLongOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.MAJFLT), 0L);
        long nonVoluntaryContextSwitches =
            ParseUtil.parseLongOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.NIVCSW), 0L);
        long voluntaryContextSwitches =
            ParseUtil.parseLongOrDefault(psMap.get(OpenBsdOperatingSystem.PsKeywords.NVCSW), 0L);
        this.contextSwitches = voluntaryContextSwitches + nonVoluntaryContextSwitches;
        this.commandLineBackup = psMap.get(OpenBsdOperatingSystem.PsKeywords.ARGS);
        return true;
    }

    private void updateThreadCount() {
        List<String> threadList = ExecutingCommand.runNative("ps -axHo tid -p " + getProcessID());
        if (!threadList.isEmpty()) {
            // Subtract 1 for header
            this.threadCount = threadList.size() - 1;
        }
        this.threadCount = 1;
    }

    /*
     * Package-private for use by OpenBsdOSThread
     */
    enum PsThreadColumns {
        TID, STATE, ETIME, CPUTIME, NIVCSW, NVCSW, MAJFLT, MINFLT, PRI, ARGS;
    }
}
