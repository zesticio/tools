
package com.zestic.system.util.platform.unix.openbsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.List;

/*
 * Reads from fstat.
 */
@ThreadSafe public final class FstatUtil {
    private FstatUtil() {
    }

    /*
     * Gets current working directory info (using {@code ps} actually).
     *
     * @param pid a process ID
     * @return the current working directory for that process.
     */
    public static String getCwd(int pid) {
        List<String> ps = ExecutingCommand.runNative("ps -axwwo cwd -p " + pid);
        if (!ps.isEmpty()) {
            return ps.get(1);
        }
        return "";
    }

    /*
     * Gets open number of files.
     *
     * @param pid The process ID
     * @return the number of open files.
     */
    public static long getOpenFiles(int pid) {
        long fd = 0L;
        List<String> fstat = ExecutingCommand.runNative("fstat -sp " + pid);
        for (String line : fstat) {
            String[] split = ParseUtil.whitespaces.split(line.trim(), 11);
            if (split.length == 11 && !"pipe".contains(split[4]) && !"unix".contains(split[4])) {
                fd++;
            }
        }
        // subtract 1 for header row
        return fd - 1;
    }
}
