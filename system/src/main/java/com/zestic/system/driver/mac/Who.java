
package com.zestic.system.driver.mac;

import com.sun.jna.Native;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.jna.platform.mac.SystemB;
import com.zestic.system.jna.platform.unix.CLibrary;
import com.zestic.system.software.os.OSSession;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
 * Utility to query logged in users.
 */
@ThreadSafe public final class Who {

    private static final SystemB SYS = SystemB.INSTANCE;

    private Who() {
    }

    /*
     * Query {@code getutxent} to get logged in users.
     *
     * @return A list of logged in user sessions
     */
    public static synchronized List<OSSession> queryUtxent() {
        List<OSSession> whoList = new ArrayList<>();
        SystemB.MacUtmpx ut;
        // Rewind
        SYS.setutxent();
        try { // Iterate
            while ((ut = SYS.getutxent()) != null) {
                if (ut.ut_type == CLibrary.USER_PROCESS || ut.ut_type == CLibrary.LOGIN_PROCESS) {
                    String user = Native.toString(ut.ut_user, StandardCharsets.US_ASCII);
                    String device = Native.toString(ut.ut_line, StandardCharsets.US_ASCII);
                    String host = Native.toString(ut.ut_host, StandardCharsets.US_ASCII);
                    long loginTime = ut.ut_tv.tv_sec.longValue() * 1000L + ut.ut_tv.tv_usec / 1000L;
                    // Sanity check. If errors, default to who command line
                    if (user.isEmpty() || device.isEmpty() || loginTime < 0
                        || loginTime > System.currentTimeMillis()) {
                        return com.zestic.system.driver.unix.Who.queryWho();
                    }
                    whoList.add(new OSSession(user, device, loginTime, host));
                }
            }
        } finally {
            // Close
            SYS.endutxent();
        }
        return whoList;
    }
}
