
package com.zestic.system.driver.unix.aix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.ExecutingCommand;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Utility to query logged in users.
 */
@ThreadSafe public final class Who {

    // sample format:
    // system boot 2020-06-16 09:12
    private static final Pattern BOOT_FORMAT_AIX =
        Pattern.compile("\\D+(\\d{4}-\\d{2}-\\d{2})\\s+(\\d{2}:\\d{2}).*");
    private static final DateTimeFormatter BOOT_DATE_FORMAT_AIX =
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private Who() {
    }

    /*
     * Query {@code who -b} to get boot time
     *
     * @return Boot time in milliseconds since the epoch
     */
    public static long queryBootTime() {
        String s = ExecutingCommand.getFirstAnswer("/usr/bin/who -b");
        Matcher m = BOOT_FORMAT_AIX.matcher(s);
        if (m.matches()) {
            try {
                return LocalDateTime.parse(m.group(1) + " " + m.group(2), BOOT_DATE_FORMAT_AIX)
                    .atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            } catch (DateTimeParseException | NullPointerException e) {
                // Shouldn't happen with regex matching
            }
        }
        return 0L;
    }
}
