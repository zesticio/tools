
package com.zestic.system.util.platform.linux;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.GlobalConfig;

import java.io.File;

/*
 * Provides constants for paths in the {@code /proc} filesystem on Linux.
 * <p>
 * If the user desires to configure a custom {@code /proc} path, it must be
 * declared in the OSHI configuration file or updated in the
 * {@link GlobalConfig} class prior to initializing this class.
 */
@ThreadSafe public final class ProcPath {

    /*
     * The /proc filesystem location.
     */
    public static final String PROC = queryProcConfig();

    public static final String ASOUND = PROC + "/asound/";
    public static final String CPUINFO = PROC + "/cpuinfo";
    public static final String DISKSTATS = PROC + "/diskstats";
    public static final String MEMINFO = PROC + "/meminfo";
    public static final String MOUNTS = PROC + "/mounts";
    public static final String NET = PROC + "/net";
    public static final String PID_CMDLINE = PROC + "/%d/cmdline";
    public static final String PID_CWD = PROC + "/%d/cwd";
    public static final String PID_EXE = PROC + "/%d/exe";
    public static final String PID_ENVIRON = PROC + "/%d/environ";
    public static final String PID_FD = PROC + "/%d/fd";
    public static final String PID_IO = PROC + "/%d/io";
    public static final String PID_STAT = PROC + "/%d/stat";
    public static final String PID_STATM = PROC + "/%d/statm";
    public static final String PID_STATUS = PROC + "/%d/status";
    public static final String SELF_STAT = PROC + "/self/stat";
    public static final String STAT = PROC + "/stat";
    public static final String SYS_FS_FILE_NR = PROC + "/sys/fs/file-nr";
    public static final String TASK_PATH = PROC + "/%d/task";
    public static final String TASK_COMM = TASK_PATH + "/%d/comm";
    public static final String TASK_STATUS = TASK_PATH + "/%d/status";
    public static final String TASK_STAT = TASK_PATH + "/%d/stat";
    public static final String UPTIME = PROC + "/uptime";
    public static final String VERSION = PROC + "/version";
    public static final String VMSTAT = PROC + "/vmstat";

    private ProcPath() {
    }

    private static String queryProcConfig() {
        String procPath = GlobalConfig.get("com.zestic.system.util.proc.path", "/proc");
        // Ensure prefix begins with path separator, but doesn't end with one
        procPath = '/' + procPath.replaceAll("/$|^/", "");
        if (!new File(procPath).exists()) {
            throw new GlobalConfig.PropertyException("com.zestic.system.util.proc.path",
                "The path does not exist");
        }
        return procPath;
    }
}
