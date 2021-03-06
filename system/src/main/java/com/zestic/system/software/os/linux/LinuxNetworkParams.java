
package com.zestic.system.software.os.linux;

import com.sun.jna.Native;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.ptr.PointerByReference;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.jna.platform.linux.LinuxLibc;
import com.zestic.system.jna.platform.unix.CLibrary;
import com.zestic.system.software.common.AbstractNetworkParams;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static com.sun.jna.platform.unix.LibCAPI.HOST_NAME_MAX;

/*
 * LinuxNetworkParams class.
 */
@ThreadSafe final class LinuxNetworkParams extends AbstractNetworkParams {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);

    private static final LinuxLibc LIBC = LinuxLibc.INSTANCE;

    private static final String IPV4_DEFAULT_DEST = "0.0.0.0"; // NOSONAR
    private static final String IPV6_DEFAULT_DEST = "::/0";

    @Override public String getDomainName() {
        CLibrary.Addrinfo hint = new CLibrary.Addrinfo();
        hint.ai_flags = CLibrary.AI_CANONNAME;
        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.error("Unknown host exception when getting address of local host: {}" +
                e.getMessage());
            return "";
        }
        PointerByReference ptr = new PointerByReference();
        int res = LIBC.getaddrinfo(hostname, null, hint, ptr);
        if (res > 0) {
            LOG.error("Failed getaddrinfo(): {}" + LIBC.gai_strerror(res));
            return "";
        }
        CLibrary.Addrinfo info = new CLibrary.Addrinfo(ptr.getValue());
        String canonname = info.ai_canonname.trim();
        LIBC.freeaddrinfo(ptr.getValue());
        return canonname;
    }

    @Override public String getHostName() {
        byte[] hostnameBuffer = new byte[HOST_NAME_MAX + 1];
        if (0 != LibC.INSTANCE.gethostname(hostnameBuffer, hostnameBuffer.length)) {
            return super.getHostName();
        }
        return Native.toString(hostnameBuffer);
    }

    @Override public String getIpv4DefaultGateway() {
        List<String> routes = ExecutingCommand.runNative("route -A inet -n");
        if (routes.size() <= 2) {
            return "";
        }

        String gateway = "";
        int minMetric = Integer.MAX_VALUE;

        for (int i = 2; i < routes.size(); i++) {
            String[] fields = ParseUtil.whitespaces.split(routes.get(i));
            if (fields.length > 4 && fields[0].equals(IPV4_DEFAULT_DEST)) {
                boolean isGateway = fields[3].indexOf('G') != -1;
                int metric = ParseUtil.parseIntOrDefault(fields[4], Integer.MAX_VALUE);
                if (isGateway && metric < minMetric) {
                    minMetric = metric;
                    gateway = fields[1];
                }
            }
        }
        return gateway;
    }

    @Override public String getIpv6DefaultGateway() {
        List<String> routes = ExecutingCommand.runNative("route -A inet6 -n");
        if (routes.size() <= 2) {
            return "";
        }

        String gateway = "";
        int minMetric = Integer.MAX_VALUE;

        for (int i = 2; i < routes.size(); i++) {
            String[] fields = ParseUtil.whitespaces.split(routes.get(i));
            if (fields.length > 3 && fields[0].equals(IPV6_DEFAULT_DEST)) {
                boolean isGateway = fields[2].indexOf('G') != -1;
                int metric = ParseUtil.parseIntOrDefault(fields[3], Integer.MAX_VALUE);
                if (isGateway && metric < minMetric) {
                    minMetric = metric;
                    gateway = fields[1];
                }
            }
        }
        return gateway;
    }
}
