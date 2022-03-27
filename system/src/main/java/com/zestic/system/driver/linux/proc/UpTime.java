
package com.zestic.system.driver.linux.proc;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.FileUtil;
import com.zestic.system.util.platform.linux.ProcPath;

/*
 * Utility to read system uptime from {@code /proc/uptime}
 */
@ThreadSafe public final class UpTime {

    private UpTime() {
    }

    /*
     * Parses the first value in {@code /proc/uptime} for seconds since boot
     *
     * @return Seconds since boot
     */
    public static double getSystemUptimeSeconds() {
        String uptime = FileUtil.getStringFromFile(ProcPath.UPTIME);
        int spaceIndex = uptime.indexOf(' ');
        try {
            if (spaceIndex < 0) {
                // No space, error
                return 0d;
            } else {
                return Double.parseDouble(uptime.substring(0, spaceIndex));
            }
        } catch (NumberFormatException nfe) {
            return 0d;
        }
    }
}
