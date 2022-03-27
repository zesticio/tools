
package com.zestic.system.software.os.unix.solaris;

import com.sun.jna.Native;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.jna.platform.unix.solaris.SolarisLibc;
import com.zestic.system.software.common.AbstractNetworkParams;
import com.zestic.system.util.ExecutingCommand;

import static com.sun.jna.platform.unix.LibCAPI.HOST_NAME_MAX;

/*
 * SolarisNetworkParams class.
 */
@ThreadSafe final class SolarisNetworkParams extends AbstractNetworkParams {

    private static final SolarisLibc LIBC = SolarisLibc.INSTANCE;

    @Override public String getHostName() {
        byte[] hostnameBuffer = new byte[HOST_NAME_MAX + 1];
        if (0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length)) {
            return super.getHostName();
        }
        return Native.toString(hostnameBuffer);
    }

    @Override public String getIpv4DefaultGateway() {
        return searchGateway(ExecutingCommand.runNative("route get -inet default"));
    }

    @Override public String getIpv6DefaultGateway() {
        return searchGateway(ExecutingCommand.runNative("route get -inet6 default"));
    }
}
