
package com.zestic.system.driver.unix.freebsd.disk;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.ExecutingCommand;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Utility to query mount
 */
@ThreadSafe public final class Mount {

    private static final String MOUNT_CMD = "mount";
    private static final Pattern MOUNT_PATTERN = Pattern.compile("/dev/(\\S+p\\d+) on (\\S+) .*");

    private Mount() {
    }

    /*
     * Query mount to map partitions to mount points
     *
     * @return A map with partitions as the key and mount points as the value
     */
    public static Map<String, String> queryPartitionToMountMap() {
        // Parse 'mount' to map partitions to mount point
        Map<String, String> mountMap = new HashMap<>();
        for (String mnt : ExecutingCommand.runNative(MOUNT_CMD)) {
            Matcher m = MOUNT_PATTERN.matcher(mnt);
            if (m.matches()) {
                mountMap.put(m.group(1), m.group(2));
            }
        }
        return mountMap;
    }
}
