
package com.zestic.system.software.os.unix.freebsd;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.jna.platform.unix.CLibrary;
import com.zestic.system.jna.platform.unix.freebsd.FreeBsdLibc;
import com.zestic.system.software.common.AbstractNetworkParams;
import com.zestic.system.util.ExecutingCommand;
import org.apache.log4j.Priority;

import static com.sun.jna.platform.unix.LibCAPI.HOST_NAME_MAX;

/*
 * FreeBsdNetworkParams class.
 */
@ThreadSafe final class FreeBsdNetworkParams extends AbstractNetworkParams {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.LogManager.getLogger(AixNetworkIF.class);

    private static final FreeBsdLibc LIBC = FreeBsdLibc.INSTANCE;

    @Override public String getDomainName() {
        CLibrary.Addrinfo hint = new CLibrary.Addrinfo();
        hint.ai_flags = CLibrary.AI_CANONNAME;
        String hostname = getHostName();

        PointerByReference ptr = new PointerByReference();
        int res = LIBC.getaddrinfo(hostname, null, hint, ptr);
        if (res > 0) {
            if (LOG.isEnabledFor(Priority.ERROR)) {
                LOG.warn("Failed getaddrinfo(): {}" + LIBC.gai_strerror(res));
            }
            return "";
        }
        CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
        String canonname = info.ai_canonname.trim();
        LIBC.freeaddrinfo(ptr.getValue());
        return canonname;
    }

    @Override public String getHostName() {
        byte[] hostnameBuffer = new byte[HOST_NAME_MAX + 1];
        if (0 != LIBC.gethostname(hostnameBuffer, hostnameBuffer.length)) {
            return super.getHostName();
        }
        return Native.toString(hostnameBuffer);
    }

    @Override public String getIpv4DefaultGateway() {
        return searchGateway(ExecutingCommand.runNative("route -4 get default"));
    }

    @Override public String getIpv6DefaultGateway() {
        return searchGateway(ExecutingCommand.runNative("route -6 get default"));
    }
}
