
package com.zestic.system.software.os.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_partition_config_t;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_process_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.aix.Uptime;
import com.zestic.system.driver.unix.aix.Who;
import com.zestic.system.driver.unix.aix.perfstat.PerfstatConfig;
import com.zestic.system.driver.unix.aix.perfstat.PerfstatProcess;
import com.zestic.system.jna.platform.unix.aix.AixLibc;
import com.zestic.system.software.common.AbstractOperatingSystem;
import com.zestic.system.software.os.*;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.Memoizer;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.Util;
import com.zestic.system.util.tuples.Pair;

import java.io.File;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*
 * AIX (Advanced Interactive eXecutive) is a series of proprietary Unix
 * operating systems developed and sold by IBM for several of its computer
 * platforms.
 */
@ThreadSafe public class AixOperatingSystem extends AbstractOperatingSystem {

    static final String PS_COMMAND_ARGS =
        Arrays.stream(PsKeywords.values()).map(Enum::name).map(String::toLowerCase)
            .collect(Collectors.joining(","));
    private static final long BOOTTIME = querySystemBootTimeMillis() / 1000L;
    private final Supplier<perfstat_partition_config_t> config =
        Memoizer.memoize(PerfstatConfig::queryConfig);
    Supplier<perfstat_process_t[]> procCpu =
        Memoizer.memoize(PerfstatProcess::queryProcesses, Memoizer.defaultExpiration());

    private static long querySystemBootTimeMillis() {
        long bootTime = Who.queryBootTime();
        if (bootTime >= 1000L) {
            return bootTime;
        }
        return System.currentTimeMillis() - Uptime.queryUpTime();
    }

    @Override public String queryManufacturer() {
        return "IBM";
    }

    @Override public Pair<String, OSVersionInfo> queryFamilyVersionInfo() {
        perfstat_partition_config_t cfg = config.get();

        String systemName = System.getProperty("os.name");
        String archName = System.getProperty("os.arch");
        String versionNumber = System.getProperty("os.version");
        if (Util.isBlank(versionNumber)) {
            versionNumber = ExecutingCommand.getFirstAnswer("oslevel");
        }
        String releaseNumber = Native.toString(cfg.OSBuild);
        if (Util.isBlank(releaseNumber)) {
            releaseNumber = ExecutingCommand.getFirstAnswer("oslevel -s");
        } else {
            // strip leading date
            int idx = releaseNumber.lastIndexOf(' ');
            if (idx > 0 && idx < releaseNumber.length()) {
                releaseNumber = releaseNumber.substring(idx + 1);
            }
        }
        return new Pair<>(systemName, new OSVersionInfo(versionNumber, archName, releaseNumber));
    }

    @Override protected int queryBitness(int jvmBitness) {
        if (jvmBitness == 64) {
            return 64;
        }
        // 9th bit of conf is 64-bit kernel
        return (config.get().conf & 0x0080_0000) > 0 ? 64 : 32;
    }

    @Override public FileSystem getFileSystem() {
        return new AixFileSystem();
    }

    @Override public InternetProtocolStats getInternetProtocolStats() {
        return new AixInternetProtocolStats();
    }

    @Override public List<OSProcess> queryAllProcesses() {
        return getProcessListFromPS("ps -A -o " + PS_COMMAND_ARGS, -1);
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

    @Override public OSProcess getProcess(int pid) {
        List<OSProcess> procs = getProcessListFromPS("ps -o " + PS_COMMAND_ARGS + " -p ", pid);
        if (procs.isEmpty()) {
            return null;
        }
        return procs.get(0);
    }

    private List<OSProcess> getProcessListFromPS(String psCommand, int pid) {
        perfstat_process_t[] perfstat = procCpu.get();
        List<String> procList = ExecutingCommand.runNative(psCommand + (pid < 0 ? "" : pid));
        if (procList.isEmpty() || procList.size() < 2) {
            return Collections.emptyList();
        }
        // Parse array to map of user/system times
        Map<Integer, Pair<Long, Long>> cpuMap = new HashMap<>();
        for (perfstat_process_t stat : perfstat) {
            cpuMap.put((int) stat.pid, new Pair<>((long) stat.ucpu_time, (long) stat.scpu_time));
        }
        // remove header row
        procList.remove(0);
        // Fill list
        List<OSProcess> procs = new ArrayList<>();
        for (String proc : procList) {
            Map<PsKeywords, String> psMap =
                ParseUtil.stringToEnumMap(PsKeywords.class, proc.trim(), ' ');
            // Check if last (thus all) value populated
            if (psMap.containsKey(PsKeywords.ARGS)) {
                procs.add(new AixOSProcess(
                    pid < 0 ? ParseUtil.parseIntOrDefault(psMap.get(PsKeywords.PID), 0) : pid,
                    psMap, cpuMap, procCpu));
            }
        }
        return procs;
    }

    @Override public int getProcessId() {
        return AixLibc.INSTANCE.getpid();
    }

    @Override public int getProcessCount() {
        return procCpu.get().length;
    }

    @Override public int getThreadCount() {
        long tc = 0L;
        for (perfstat_process_t proc : procCpu.get()) {
            tc += proc.num_threads;
        }
        return (int) tc;
    }

    @Override public long getSystemUptime() {
        return System.currentTimeMillis() / 1000L - BOOTTIME;
    }

    @Override public long getSystemBootTime() {
        return BOOTTIME;
    }

    @Override public NetworkParams getNetworkParams() {
        return new AixNetworkParams();
    }

    @Override public List<OSService> getServices() {
        List<OSService> services = new ArrayList<>();
        // Get system services from lssrc command
        /*-
         Output:
         Subsystem         Group            PID          Status
            platform_agent                    2949214      active
            cimsys                            2490590      active
            snmpd            tcpip            2883698      active
            syslogd          ras              2359466      active
            sendmail         mail             3145828      active
            portmap          portmap          2818188      active
            inetd            tcpip            2752656      active
            lpd              spooler                       inoperative
                        ...
         */
        List<String> systemServicesInfoList = ExecutingCommand.runNative("lssrc -a");
        if (systemServicesInfoList.size() > 1) {
            systemServicesInfoList.remove(0); // remove header
            for (String systemService : systemServicesInfoList) {
                String[] serviceSplit = ParseUtil.whitespaces.split(systemService.trim());
                if (systemService.contains("active")) {
                    if (serviceSplit.length == 4) {
                        services.add(new OSService(serviceSplit[0],
                            ParseUtil.parseIntOrDefault(serviceSplit[2], 0),
                            OSService.State.RUNNING));
                    } else if (serviceSplit.length == 3) {
                        services.add(new OSService(serviceSplit[0],
                            ParseUtil.parseIntOrDefault(serviceSplit[1], 0),
                            OSService.State.RUNNING));
                    }
                } else if (systemService.contains("inoperative")) {
                    services.add(new OSService(serviceSplit[0], 0, OSService.State.STOPPED));
                }
            }
        }
        // Get installed services from /etc/rc.d/init.d
        File dir = new File("/etc/rc.d/init.d");
        File[] listFiles;
        if (dir.exists() && dir.isDirectory() && (listFiles = dir.listFiles()) != null) {
            for (File file : listFiles) {
                String installedService =
                    ExecutingCommand.getFirstAnswer(file.getAbsolutePath() + " status");
                // Apache httpd daemon is running with PID 3997858.
                if (installedService.contains("running")) {
                    services.add(
                        new OSService(file.getName(), ParseUtil.parseLastInt(installedService, 0),
                            OSService.State.RUNNING));
                } else {
                    services.add(new OSService(file.getName(), 0, OSService.State.STOPPED));
                }
            }
        }
        return services;
    }

    /*
     * Package-private for use by AixOSProcess
     */
    enum PsKeywords {
        ST, PID, PPID, USER, UID, GROUP, GID, THCOUNT, PRI, VSIZE, RSSIZE, ETIME, TIME, COMM, PAGEIN, ARGS;
        // ARGS must always be last
    }
}
