
package com.zestic.system.driver.unix.aix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Utility to query up time.
 */
@ThreadSafe public final class Uptime {

    private static final long MINUTE_MS = 60L * 1000L;
    private static final long HOUR_MS = 60L * MINUTE_MS;
    private static final long DAY_MS = 24L * HOUR_MS;

    // sample format:
    // 18:36pm up 10 days 8:11, 2 users, load average: 3.14, 2.74, 2.41
    private static final Pattern UPTIME_FORMAT_AIX = Pattern.compile(
        ".*\\sup\\s+((\\d+)\\s+days?,?\\s+)?\\b((\\d+):)?(\\d+)(\\s+min(utes?)?)?,\\s+\\d+\\s+user.+");

    private Uptime() {
    }

    /*
     * Query {@code uptime} to get up time
     *
     * @return Up time in milliseconds
     */
    public static long queryUpTime() {
        long uptime = 0L;
        String s = ExecutingCommand.getFirstAnswer("/usr/bin/uptime");
        Matcher m = UPTIME_FORMAT_AIX.matcher(s);
        if (m.matches()) {
            if (m.group(2) != null) {
                uptime += ParseUtil.parseLongOrDefault(m.group(2), 0L) * DAY_MS;
            }
            if (m.group(4) != null) {
                uptime += ParseUtil.parseLongOrDefault(m.group(4), 0L) * HOUR_MS;
            }
            uptime += ParseUtil.parseLongOrDefault(m.group(5), 0L) * MINUTE_MS;
        }
        return uptime;
    }
}
