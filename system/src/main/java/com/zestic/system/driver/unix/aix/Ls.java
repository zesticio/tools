
package com.zestic.system.driver.unix.aix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.tuples.Pair;

import java.util.HashMap;
import java.util.Map;

/*
 * Utility to query ls
 */
@ThreadSafe public final class Ls {

    private Ls() {
    }

    /*
     * Query {@code ls} to get parition info
     *
     * @return A map of device name to a major-minor pair
     */
    public static Map<String, Pair<Integer, Integer>> queryDeviceMajorMinor() {
        // Map major and minor from ls
        /*-
         $ ls -l /dev
        brw-rw----  1 root system 10,  5 Sep 12  2017 hd2
        brw-------  1 root system 20,  0 Jun 28  1970 hdisk0
         */
        Map<String, Pair<Integer, Integer>> majMinMap = new HashMap<>();
        for (String s : ExecutingCommand.runNative("ls -l /dev")) {
            // Filter to block devices
            if (!s.isEmpty() && s.charAt(0) == 'b') {
                // Device name is last space-delim string
                int idx = s.lastIndexOf(' ');
                if (idx > 0 && idx < s.length()) {
                    String device = s.substring(idx + 1);
                    int major = ParseUtil.getNthIntValue(s, 2);
                    int minor = ParseUtil.getNthIntValue(s, 3);
                    majMinMap.put(device, new Pair<>(major, minor));
                }
            }
        }
        return majMinMap;
    }
}
