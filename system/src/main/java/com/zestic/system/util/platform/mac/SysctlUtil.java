
package com.zestic.system.util.platform.mac;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.jna.platform.mac.SystemB;

/*
 * Provides access to sysctl calls on macOS
 */
@ThreadSafe
public final class SysctlUtil {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(SysctlUtil.class);

    private static final String SYSCTL_FAIL = "Failed sysctl call: {}, Error code: {}";

    private SysctlUtil() {
    }

    /*
     * Executes a sysctl call with an int result
     *
     * @param name name of the sysctl
     * @param def  default int value
     * @return The int result of the call if successful; the default otherwise
     */
    public static int sysctl(String name, int def) {
        size_t.ByReference size = new size_t.ByReference(com.sun.jna.platform.mac.SystemB.INT_SIZE);
        Pointer p = new Memory(size.longValue());
        if (0 != SystemB.INSTANCE.sysctlbyname(name, p, size, null, size_t.ZERO)) {
            logger.error(SYSCTL_FAIL + " " + name + " " + Native.getLastError());
            return def;
        }
        return p.getInt(0);
    }

    /*
     * Executes a sysctl call with a long result
     *
     * @param name name of the sysctl
     * @param def  default long value
     * @return The long result of the call if successful; the default otherwise
     */
    public static long sysctl(String name, long def) {
        size_t.ByReference size =
                new size_t.ByReference(com.sun.jna.platform.mac.SystemB.UINT64_SIZE);
        Pointer p = new Memory(size.longValue());
        if (0 != SystemB.INSTANCE.sysctlbyname(name, p, size, null, size_t.ZERO)) {
            logger.error(SYSCTL_FAIL + " " + name + " " + Native.getLastError());
            return def;
        }
        return p.getLong(0);
    }

    /*
     * Executes a sysctl call with a String result
     *
     * @param name name of the sysctl
     * @param def  default String value
     * @return The String result of the call if successful; the default otherwise
     */
    public static String sysctl(String name, String def) {
        // Call first time with null pointer to get value of size
        size_t.ByReference size = new size_t.ByReference();
        if (0 != SystemB.INSTANCE.sysctlbyname(name, null, size, null, size_t.ZERO)) {
            logger.error(SYSCTL_FAIL + " " + name + " " + Native.getLastError());
            return def;
        }
        // Add 1 to size for null terminated string
        Pointer p = new Memory(size.longValue() + 1L);
        if (0 != SystemB.INSTANCE.sysctlbyname(name, p, size, null, size_t.ZERO)) {
            logger.error(SYSCTL_FAIL + " " + name + " " + Native.getLastError());
            return def;
        }
        return p.getString(0);
    }

    /*
     * Executes a sysctl call with a Structure result
     *
     * @param name   name of the sysctl
     * @param struct structure for the result
     * @return True if structure is successfuly populated, false otherwise
     */
    public static boolean sysctl(String name, Structure struct) {
        if (0 != SystemB.INSTANCE.sysctlbyname(name, struct.getPointer(),
                new size_t.ByReference(struct.size()), null, size_t.ZERO)) {
            logger.error(SYSCTL_FAIL + " " + name + " " + Native.getLastError());
            return false;
        }
        struct.read();
        return true;
    }

    /*
     * Executes a sysctl call with a Pointer result
     *
     * @param name name of the sysctl
     * @return An allocated memory buffer containing the result on success, null
     * otherwise. Its value on failure is undefined.
     */
    public static Memory sysctl(String name) {
        size_t.ByReference size = new size_t.ByReference();
        if (0 != SystemB.INSTANCE.sysctlbyname(name, null, size, null, size_t.ZERO)) {
            logger.error(SYSCTL_FAIL + " " + name + " " + Native.getLastError());
            return null;
        }
        Memory m = new Memory(size.longValue());
        if (0 != SystemB.INSTANCE.sysctlbyname(name, m, size, null, size_t.ZERO)) {
            logger.error(SYSCTL_FAIL + " " + name + " " + Native.getLastError());
            return null;
        }
        return m;
    }
}
