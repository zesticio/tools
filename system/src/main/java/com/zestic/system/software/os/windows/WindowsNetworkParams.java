
package com.zestic.system.software.os.windows;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.IPHlpAPI;
import com.sun.jna.platform.win32.IPHlpAPI.FIXED_INFO;
import com.sun.jna.platform.win32.IPHlpAPI.IP_ADDR_STRING;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import com.sun.jna.platform.win32.WinError;
import com.sun.jna.ptr.IntByReference;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.software.common.AbstractNetworkParams;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
 * WindowsNetworkParams class.
 */
@ThreadSafe final class WindowsNetworkParams extends AbstractNetworkParams {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.LogManager.getLogger(AixNetworkIF.class);

    private static final int COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED = 3;

    private static String parseIpv4Route() {
        List<String> lines = ExecutingCommand.runNative("route print -4 0.0.0.0");
        for (String line : lines) {
            String[] fields = ParseUtil.whitespaces.split(line.trim());
            if (fields.length > 2 && "0.0.0.0".equals(fields[0])) {
                return fields[2];
            }
        }
        return "";
    }

    private static String parseIpv6Route() {
        List<String> lines = ExecutingCommand.runNative("route print -6 ::/0");
        for (String line : lines) {
            String[] fields = ParseUtil.whitespaces.split(line.trim());
            if (fields.length > 3 && "::/0".equals(fields[2])) {
                return fields[3];
            }
        }
        return "";
    }

    @Override public String getDomainName() {
        char[] buffer = new char[256];
        IntByReference bufferSize = new IntByReference(buffer.length);
        if (!Kernel32.INSTANCE.GetComputerNameEx(COMPUTER_NAME_DNS_DOMAIN_FULLY_QUALIFIED, buffer,
            bufferSize)) {
            LOG.error("Failed to get dns domain name. Error code: {}" +
                Kernel32.INSTANCE.GetLastError());
            return "";
        }
        return Native.toString(buffer);
    }

    @Override public String[] getDnsServers() {
        IntByReference bufferSize = new IntByReference();
        int ret = IPHlpAPI.INSTANCE.GetNetworkParams(null, bufferSize);
        if (ret != WinError.ERROR_BUFFER_OVERFLOW) {
            LOG.error("Failed to get network parameters buffer size. Error code: {}" + ret);
            return new String[0];
        }

        Memory buffer = new Memory(bufferSize.getValue());
        ret = IPHlpAPI.INSTANCE.GetNetworkParams(buffer, bufferSize);
        if (ret != 0) {
            LOG.error("Failed to get network parameters. Error code: {}" + ret);
            return new String[0];
        }
        FIXED_INFO fixedInfo = new FIXED_INFO(buffer);

        List<String> list = new ArrayList<>();
        IP_ADDR_STRING dns = fixedInfo.DnsServerList;
        while (dns != null) {
            // a char array of size 16.
            // This array holds an IPv4 address in dotted decimal notation.
            String addr = Native.toString(dns.IpAddress.String, StandardCharsets.US_ASCII);
            int nullPos = addr.indexOf(0);
            if (nullPos != -1) {
                addr = addr.substring(0, nullPos);
            }
            list.add(addr);
            dns = dns.Next;
        }
        return list.toArray(new String[0]);
    }

    @Override public String getHostName() {
        return Kernel32Util.getComputerName();
    }

    @Override public String getIpv4DefaultGateway() {
        return parseIpv4Route();
    }

    @Override public String getIpv6DefaultGateway() {
        return parseIpv6Route();
    }
}
