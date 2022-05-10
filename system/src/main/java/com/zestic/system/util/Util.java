
package com.zestic.system.util;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;

/*
 * General utility methods
 */
@ThreadSafe
public final class Util {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);

    private Util() {
    }

    /*
     * Sleeps for the specified number of milliseconds.
     *
     * @param ms How long to sleep
     */
    public static void sleep(long ms) {
        try {
            LOG.trace("Sleeping for {" + ms + "} ms");
            Thread.sleep(ms);
        } catch (InterruptedException e) { // NOSONAR squid:S2142
            LOG.warn("Interrupted while sleeping for {" + ms + "} ms: {" + e.getMessage() + "}");
            Thread.currentThread().interrupt();
        }
    }

    /*
     * Tests if a String matches another String with a wildcard pattern.
     *
     * @param text    The String to test
     * @param pattern The String containing a wildcard pattern where ? represents a
     *                single character and * represents any number of characters. If the
     *                first character of the pattern is a carat (^) the test is
     *                performed against the remaining characters and the result of the
     *                test is the opposite.
     * @return True if the String matches or if the first character is ^ and the
     * remainder of the String does not match.
     */
    public static boolean wildcardMatch(String text, String pattern) {
        if (pattern.length() > 0 && pattern.charAt(0) == '^') {
            return !wildcardMatch(text, pattern.substring(1));
        }
        return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
    }

    /*
     * Tests if a String is either null or empty.
     *
     * @param s The string to test
     * @return True if the String is either null or empty.
     */
    public static boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }

    /*
     * Tests if a String is either null or empty or the unknown constant.
     *
     * @param s The string to test
     * @return True if the String is either null or empty or the unknown constant.
     */
    public static boolean isBlankOrUnknown(String s) {
        return isBlank(s) || Constants.UNKNOWN.equals(s);
    }
}
