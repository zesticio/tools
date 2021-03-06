
package com.zestic.system.util;

import com.zestic.system.annotation.concurrent.ThreadSafe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Reads from lsof into a map
 */
@ThreadSafe public final class LsofUtil {

    private LsofUtil() {
    }

    /*
     * Gets a map containing current working directory info
     *
     * @param pid a process ID, optional
     * @return a map of process IDs to their current working directory. If
     * {@code pid} is a negative number, all processes are returned;
     * otherwise the map may contain only a single element for {@code pid}
     */
    public static Map<Integer, String> getCwdMap(int pid) {
        List<String> lsof =
            ExecutingCommand.runNative("lsof -F n -d cwd" + (pid < 0 ? "" : " -p " + pid));
        Map<Integer, String> cwdMap = new HashMap<>();
        Integer key = -1;
        for (String line : lsof) {
            if (line.isEmpty()) {
                continue;
            }
            switch (line.charAt(0)) {
                case 'p':
                    key = ParseUtil.parseIntOrDefault(line.substring(1), -1);
                    break;
                case 'n':
                    cwdMap.put(key, line.substring(1));
                    break;
                case 'f':
                    // ignore the 'cwd' file descriptor
                default:
                    break;
            }
        }
        return cwdMap;
    }

    /*
     * Gets current working directory info
     *
     * @param pid a process ID
     * @return the current working directory for that process.
     */
    public static String getCwd(int pid) {
        List<String> lsof = ExecutingCommand.runNative("lsof -F n -d cwd -p " + pid);
        for (String line : lsof) {
            if (!line.isEmpty() && line.charAt(0) == 'n') {
                return line.substring(1).trim();
            }
        }
        return "";
    }

    /*
     * Gets open files
     *
     * @param pid The process ID
     * @return the number of open files.
     */
    public static long getOpenFiles(int pid) {
        int openFiles = ExecutingCommand.runNative("lsof -p " + pid).size();
        // If nonzero, subtract 1 from size for header
        return openFiles > 0 ? openFiles - 1L : 0L;
    }
}
