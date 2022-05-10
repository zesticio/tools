
package com.zestic.system.software.os.mac;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.jna.platform.mac.SystemB;
import com.zestic.system.jna.platform.unix.CLibrary;
import com.zestic.system.jna.platform.unix.CLibrary.Addrinfo;
import com.zestic.system.software.common.AbstractNetworkParams;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static com.sun.jna.platform.unix.LibCAPI.HOST_NAME_MAX;

/*
 * MacNetworkParams class.
 */
@ThreadSafe
final class MacNetworkParams extends AbstractNetworkParams {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MacNetworkParams.class);

    private static final SystemB SYS = SystemB.INSTANCE;

    private static final String IPV6_ROUTE_HEADER = "Internet6:";

    private static final String DEFAULT_GATEWAY = "default";

    @Override
    public String getDomainName() {
        Addrinfo hint = new Addrinfo();
        hint.ai_flags = CLibrary.AI_CANONNAME;
        String hostname = "";
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            LOG.error("Unknown host exception when getting address of local host: {}" + e.getMessage());
            return "";
        }
        PointerByReference ptr = new PointerByReference();
        int res = SYS.getaddrinfo(hostname, null, hint, ptr);
        if (res > 0) {
            LOG.error("Failed getaddrinfo(): {}" + SYS.gai_strerror(res));
            return "";
        }
        Addrinfo info = new Addrinfo(ptr.getValue());
        String canonname = info.ai_canonname.trim();
        SYS.freeaddrinfo(ptr.getValue());
        return canonname;
    }

    @Override
    public String getHostName() {
        byte[] hostnameBuffer = new byte[HOST_NAME_MAX + 1];
        if (0 != SYS.gethostname(hostnameBuffer, hostnameBuffer.length)) {
            return super.getHostName();
        }
        return Native.toString(hostnameBuffer);
    }

    @Override
    public String getIpv4DefaultGateway() {
        return searchGateway(ExecutingCommand.runNative("route -n get default"));
    }

    @Override
    public String getIpv6DefaultGateway() {
        List<String> lines = ExecutingCommand.runNative("netstat -nr");
        boolean v6Table = false;
        for (String line : lines) {
            if (v6Table && line.startsWith(DEFAULT_GATEWAY)) {
                String[] fields = ParseUtil.whitespaces.split(line);
                if (fields.length > 2 && fields[2].contains("G")) {
                    return fields[1].split("%")[0];
                }
            } else if (line.startsWith(IPV6_ROUTE_HEADER)) {
                v6Table = true;
            }
        }
        return "";
    }
}
