
package com.zestic.system.software.os.unix.aix;

import com.sun.jna.Native;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.jna.platform.unix.aix.AixLibc;
import com.zestic.system.software.common.AbstractNetworkParams;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import static com.sun.jna.platform.unix.LibCAPI.HOST_NAME_MAX;

/*
 * AixNetworkParams class.
 */
@ThreadSafe final class AixNetworkParams extends AbstractNetworkParams {

    private static final AixLibc LIBC = AixLibc.INSTANCE;

    private static String getDefaultGateway(String netstat) {
        /*-
        $ netstat -rnf inet
        Routing tables
        Destination        Gateway           Flags   Refs     Use  If   Exp  Groups
        *
        Route Tree for Protocol Family 2 (Internet):
        default            192.168.10.1      UG        9    873816 en0      -      -
        127/8              127.0.0.1         U         9    839480 lo0      -      -
        192.168.10.0       192.168.10.80     UHSb      0         0 en0      -      -   =>
        192.168.10/24      192.168.10.80     U         3    394820 en0      -      -
        192.168.10.80      127.0.0.1         UGHS      0         7 lo0      -      -
        192.168.10.255     192.168.10.80     UHSb      2      7466 en0      -      -
        */
        for (String line : ExecutingCommand.runNative(netstat)) {
            String[] split = ParseUtil.whitespaces.split(line);
            if (split.length > 7 && "default".equals(split[0])) {
                return split[1];
            }
        }
        return Constants.UNKNOWN;
    }

    @Override public String getHostName() {
        byte[] hostnameBuffer = new byte[HOST_NAME_MAX + 1];
        if (0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length)) {
            return super.getHostName();
        }
        return Native.toString(hostnameBuffer);
    }

    @Override public String getIpv4DefaultGateway() {
        return getDefaultGateway("netstat -rnf inet");
    }

    @Override public String getIpv6DefaultGateway() {
        return getDefaultGateway("netstat -rnf inet6");
    }
}
