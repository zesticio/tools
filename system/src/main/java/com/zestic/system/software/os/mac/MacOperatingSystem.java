
package com.zestic.system.software.os.mac;

import com.sun.jna.platform.mac.SystemB;
import com.sun.jna.platform.mac.SystemB.ProcTaskInfo;
import com.sun.jna.platform.mac.SystemB.Timeval;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.mac.Who;
import com.zestic.system.driver.mac.WindowInfo;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.software.common.AbstractOperatingSystem;
import com.zestic.system.software.os.*;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.FileUtil;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.Util;
import com.zestic.system.util.platform.mac.SysctlUtil;
import com.zestic.system.util.tuples.Pair;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/*
 * macOS, previously Mac OS X and later OS X) is a series of proprietary
 * graphical operating systems developed and marketed by Apple Inc. since 2001.
 * It is the primary operating system for Apple's Mac computers.
 */
@ThreadSafe public class MacOperatingSystem extends AbstractOperatingSystem {

    public static final String MACOS_VERSIONS_PROPERTIES =
        "com.zestic.system.macos.versions.properties";
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);
    private static final String SYSTEM_LIBRARY_LAUNCH_AGENTS = "/System/Library/LaunchAgents";
    private static final String SYSTEM_LIBRARY_LAUNCH_DAEMONS = "/System/Library/LaunchDaemons";
    private static final long BOOTTIME;

    static {
        Timeval tv = new Timeval();
        if (!SysctlUtil.sysctl("kern.boottime", tv) || tv.tv_sec.longValue() == 0L) {
            // Usually this works. If it doesn't, fall back to text parsing.
            // Boot time will be the first consecutive string of digits.
            BOOTTIME = ParseUtil.parseLongOrDefault(
                ExecutingCommand.getFirstAnswer("sysctl -n kern.boottime").split(",")[0].replaceAll(
                    "\\D", ""), System.currentTimeMillis() / 1000);
        } else {
            // tv now points to a 64-bit timeval structure for boot time.
            // First 4 bytes are seconds, second 4 bytes are microseconds
            // (we ignore)
            BOOTTIME = tv.tv_sec.longValue();
        }
    }

    private final String osXVersion;
    private final int major;
    private final int minor;
    private int maxProc = 1024;

    public MacOperatingSystem() {
        String version = System.getProperty("os.version");
        int verMajor = ParseUtil.getFirstIntValue(version);
        int verMinor = ParseUtil.getNthIntValue(version, 2);
        // Big Sur (11.x) may return 10.16
        if (verMajor == 10 && verMinor > 15) {
            String swVers = ExecutingCommand.getFirstAnswer("sw_vers -productVersion");
            if (!swVers.isEmpty()) {
                version = swVers;
            }
            verMajor = ParseUtil.getFirstIntValue(version);
            verMinor = ParseUtil.getNthIntValue(version, 2);
        }
        this.osXVersion = version;
        this.major = verMajor;
        this.minor = verMinor;
        // Set max processes
        this.maxProc = SysctlUtil.sysctl("kern.maxproc", 0x1000);
    }

    @Override public String queryManufacturer() {
        return "Apple";
    }

    @Override public Pair<String, OSVersionInfo> queryFamilyVersionInfo() {
        String family = this.major > 10 || (this.major == 10 && this.minor >= 12) ?
            "macOS" :
            System.getProperty("os.name");
        String codeName = parseCodeName();
        String buildNumber = SysctlUtil.sysctl("kern.osversion", "");
        return new Pair<>(family, new OSVersionInfo(this.osXVersion, codeName, buildNumber));
    }

    private String parseCodeName() {
        Properties verProps = FileUtil.readPropertiesFromFilename(MACOS_VERSIONS_PROPERTIES);
        String codeName = null;
        if (this.major > 10) {
            codeName = verProps.getProperty(Integer.toString(this.major));
        } else if (this.major == 10) {
            codeName = verProps.getProperty(this.major + "." + this.minor);
        }
        if (Util.isBlank(codeName)) {
//            LOG.warn("Unable to parse version {}.{} to a codename.", this.major, this.minor);
        }
        return codeName;
    }

    @Override protected int queryBitness(int jvmBitness) {
        if (jvmBitness == 64 || (this.major == 10 && this.minor > 6)) {
            return 64;
        }
        return ParseUtil.parseIntOrDefault(ExecutingCommand.getFirstAnswer("getconf LONG_BIT"), 32);
    }

    @Override public FileSystem getFileSystem() {
        return new MacFileSystem();
    }

    @Override public InternetProtocolStats getInternetProtocolStats() {
        return new MacInternetProtocolStats(isElevated());
    }

    @Override public List<OSSession> getSessions() {
        return USE_WHO_COMMAND ? super.getSessions() : Who.queryUtxent();
    }

    @Override public List<OSProcess> queryAllProcesses() {
        List<OSProcess> procs = new ArrayList<>();
        int[] pids = new int[this.maxProc];
        int numberOfProcesses = SystemB.INSTANCE.proc_listpids(SystemB.PROC_ALL_PIDS, 0, pids,
            pids.length * SystemB.INT_SIZE) / SystemB.INT_SIZE;
        for (int i = 0; i < numberOfProcesses; i++) {
            // Handle off-by-one bug in proc_listpids where the size returned
            // is: SystemB.INT_SIZE * (pids + 1)
            if (pids[i] != 0) {
                OSProcess proc = getProcess(pids[i]);
                if (proc != null) {
                    procs.add(proc);
                }
            }
        }
        return procs;
    }

    @Override public OSProcess getProcess(int pid) {
        OSProcess proc = new MacOSProcess(pid, this.minor);
        return proc.getState().equals(OSProcess.State.INVALID) ? null : proc;
    }

    @Override public List<OSProcess> queryChildProcesses(int parentPid) {
        List<OSProcess> allProcs = queryAllProcesses();
        Set<Integer> descendantPids = getChildrenOrDescendants(allProcs, parentPid, false);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID()))
            .collect(Collectors.toList());
    }

    @Override public List<OSProcess> queryDescendantProcesses(int parentPid) {
        List<OSProcess> allProcs = queryAllProcesses();
        Set<Integer> descendantPids = getChildrenOrDescendants(allProcs, parentPid, true);
        return allProcs.stream().filter(p -> descendantPids.contains(p.getProcessID()))
            .collect(Collectors.toList());
    }

    @Override public int getProcessId() {
        return SystemB.INSTANCE.getpid();
    }

    @Override public int getProcessCount() {
        return SystemB.INSTANCE.proc_listpids(SystemB.PROC_ALL_PIDS, 0, null, 0) / SystemB.INT_SIZE;
    }

    @Override public int getThreadCount() {
        // Get current pids, then slightly pad in case new process starts while
        // allocating array space
        int[] pids = new int[getProcessCount() + 10];
        int numberOfProcesses =
            SystemB.INSTANCE.proc_listpids(SystemB.PROC_ALL_PIDS, 0, pids, pids.length)
                / SystemB.INT_SIZE;
        int numberOfThreads = 0;
        ProcTaskInfo taskInfo = new ProcTaskInfo();
        for (int i = 0; i < numberOfProcesses; i++) {
            int exit = SystemB.INSTANCE.proc_pidinfo(pids[i], SystemB.PROC_PIDTASKINFO, 0, taskInfo,
                taskInfo.size());
            if (exit != -1) {
                numberOfThreads += taskInfo.pti_threadnum;
            }
        }
        return numberOfThreads;
    }

    @Override public long getSystemUptime() {
        return System.currentTimeMillis() / 1000 - BOOTTIME;
    }

    @Override public long getSystemBootTime() {
        return BOOTTIME;
    }

    @Override public NetworkParams getNetworkParams() {
        return new MacNetworkParams();
    }

    @Override public List<OSService> getServices() {
        // Get running services
        List<OSService> services = new ArrayList<>();
        Set<String> running = new HashSet<>();
        for (OSProcess p : getChildProcesses(1, ProcessFiltering.ALL_PROCESSES,
            ProcessSorting.PID_ASC, 0)) {
            OSService s = new OSService(p.getName(), p.getProcessID(), OSService.State.RUNNING);
            services.add(s);
            running.add(p.getName());
        }
        // Get Directories for stopped services
        ArrayList<File> files = new ArrayList<>();
        File dir = new File(SYSTEM_LIBRARY_LAUNCH_AGENTS);
        if (dir.exists() && dir.isDirectory()) {
            files.addAll(
                Arrays.asList(dir.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
        } else {
            LOG.error("Directory: /System/Library/LaunchAgents does not exist");
        }
        dir = new File(SYSTEM_LIBRARY_LAUNCH_DAEMONS);
        if (dir.exists() && dir.isDirectory()) {
            files.addAll(
                Arrays.asList(dir.listFiles((f, name) -> name.toLowerCase().endsWith(".plist"))));
        } else {
            LOG.error("Directory: /System/Library/LaunchDaemons does not exist");
        }
        for (File f : files) {
            // remove .plist extension
            String name = f.getName().substring(0, f.getName().length() - 6);
            int index = name.lastIndexOf('.');
            String shortName =
                (index < 0 || index > name.length() - 2) ? name : name.substring(index + 1);
            if (!running.contains(name) && !running.contains(shortName)) {
                OSService s = new OSService(name, 0, OSService.State.STOPPED);
                services.add(s);
            }
        }
        return services;
    }

    @Override public List<OSDesktopWindow> getDesktopWindows(boolean visibleOnly) {
        return WindowInfo.queryDesktopWindows(visibleOnly);
    }
}
