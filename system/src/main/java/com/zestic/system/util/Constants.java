
package com.zestic.system.util;

import com.zestic.system.annotation.concurrent.ThreadSafe;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/*
 * General constants used in multiple classes
 */
@ThreadSafe public final class Constants {

    /*
     * String to report for unknown information
     */
    public static final String UNKNOWN = "unknown";

    /*
     * The official/approved path for sysfs information. Note: /sys/class/dmi/id
     * symlinks here
     */
    public static final String SYSFS_SERIAL_PATH = "/sys/devices/virtual/dmi/id/";

    /*
     * The Unix Epoch, a default value when WMI DateTime queries return no value.
     */
    public static final OffsetDateTime UNIX_EPOCH =
        OffsetDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC);

    /*
     * Everything in this class is static, never instantiate it
     */
    private Constants() {
        throw new AssertionError();
    }
}
