
package com.zestic.system.driver.linux;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

/*
 * Utility to read info from {@code lshw}
 */
@ThreadSafe public final class Lshw {

    private Lshw() {
    }

    /*
     * Query the model from lshw
     *
     * @return The model if available, null otherwise
     */
    public static String queryModel() {
        String modelMarker = "product:";
        for (String checkLine : ExecutingCommand.runNative("lshw -C system")) {
            if (checkLine.contains(modelMarker)) {
                return checkLine.split(modelMarker)[1].trim();
            }
        }
        return null;
    }

    /*
     * Query the serial number from lshw
     *
     * @return The serial number if available, null otherwise
     */
    public static String querySerialNumber() {
        String serialMarker = "serial:";
        for (String checkLine : ExecutingCommand.runNative("lshw -C system")) {
            if (checkLine.contains(serialMarker)) {
                return checkLine.split(serialMarker)[1].trim();
            }
        }
        return null;
    }

    /*
     * Query the UUID from lshw
     *
     * @return The UUID if available, null otherwise
     */
    public static String queryUUID() {
        String uuidMarker = "uuid:";
        for (String checkLine : ExecutingCommand.runNative("lshw -C system")) {
            if (checkLine.contains(uuidMarker)) {
                return checkLine.split(uuidMarker)[1].trim();
            }
        }
        return null;
    }

    /*
     * Query the CPU capacity (max frequency) from lshw
     *
     * @return The CPU capacity (max frequency) if available, -1 otherwise
     */
    public static long queryCpuCapacity() {
        String capacityMarker = "capacity:";
        for (String checkLine : ExecutingCommand.runNative("lshw -class processor")) {
            if (checkLine.contains(capacityMarker)) {
                return ParseUtil.parseHertz(checkLine.split(capacityMarker)[1].trim());
            }
        }
        return -1L;
    }
}
