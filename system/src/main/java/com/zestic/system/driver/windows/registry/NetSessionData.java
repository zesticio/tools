
package com.zestic.system.driver.windows.registry;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Netapi32;
import com.sun.jna.platform.win32.Netapi32.SESSION_INFO_10;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.os.OSSession;

import java.util.ArrayList;
import java.util.List;

/*
 * Utility to read process data from HKEY_PERFORMANCE_DATA information with
 * backup from Performance Counters or WMI
 */
@ThreadSafe public final class NetSessionData {

    private static final Netapi32 NET = Netapi32.INSTANCE;

    private NetSessionData() {
    }

    public static List<OSSession> queryUserSessions() {
        List<OSSession> sessions = new ArrayList<>();
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        if (0 == NET.NetSessionEnum(null, null, null, 10, bufptr, Netapi32.MAX_PREFERRED_LENGTH,
            entriesread, totalentries, null)) {
            Pointer buf = bufptr.getValue();
            SESSION_INFO_10 si10 = new SESSION_INFO_10(buf);
            if (entriesread.getValue() > 0) {
                SESSION_INFO_10[] sessionInfo =
                    (SESSION_INFO_10[]) si10.toArray(entriesread.getValue());
                for (SESSION_INFO_10 si : sessionInfo) {
                    // time field is connected seconds
                    long logonTime = System.currentTimeMillis() - (1000L * si.sesi10_time);
                    sessions.add(new OSSession(si.sesi10_username, "Network session", logonTime,
                        si.sesi10_cname));
                }
            }
            NET.NetApiBufferFree(buf);
        }
        return sessions;
    }
}
