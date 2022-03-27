
package com.zestic.system.software.os.linux;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.linux.proc.ProcessStat;
import com.zestic.system.driver.unix.NetStat;
import com.zestic.system.software.common.AbstractInternetProtocolStats;
import com.zestic.system.util.FileUtil;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.linux.ProcPath;
import com.zestic.system.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.zestic.system.software.os.InternetProtocolStats.TcpState.*;

/*
 * Internet Protocol Stats implementation
 */
@ThreadSafe public class LinuxInternetProtocolStats extends AbstractInternetProtocolStats {

    private static List<IPConnection> queryConnections(String protocol, int ipver,
        Map<Integer, Integer> pidMap) {
        List<IPConnection> conns = new ArrayList<>();
        for (String s : FileUtil.readFile(
            ProcPath.NET + "/" + protocol + (ipver == 6 ? "6" : ""))) {
            if (s.indexOf(':') >= 0) {
                String[] split = ParseUtil.whitespaces.split(s.trim());
                if (split.length > 9) {
                    Pair<byte[], Integer> lAddr = parseIpAddr(split[1]);
                    Pair<byte[], Integer> fAddr = parseIpAddr(split[2]);
                    TcpState state = stateLookup(ParseUtil.hexStringToInt(split[3], 0));
                    Pair<Integer, Integer> txQrxQ = parseHexColonHex(split[4]);
                    int inode = ParseUtil.parseIntOrDefault(split[9], 0);
                    conns.add(
                        new IPConnection(protocol + ipver, lAddr.getA(), lAddr.getB(), fAddr.getA(),
                            fAddr.getB(), state, txQrxQ.getA(), txQrxQ.getB(),
                            pidMap.getOrDefault(inode, -1)));
                }
            }
        }
        return conns;
    }

    private static Pair<byte[], Integer> parseIpAddr(String s) {
        int colon = s.indexOf(':');
        if (colon > 0 && colon < s.length()) {
            byte[] first = ParseUtil.hexStringToByteArray(s.substring(0, colon));
            // Bytes are in __be32 endianness. we must invert each set of 4 bytes
            for (int i = 0; i + 3 < first.length; i += 4) {
                byte tmp = first[i];
                first[i] = first[i + 3];
                first[i + 3] = tmp;
                tmp = first[i + 1];
                first[i + 1] = first[i + 2];
                first[i + 2] = tmp;
            }
            int second = ParseUtil.hexStringToInt(s.substring(colon + 1), 0);
            return new Pair<>(first, second);
        }
        return new Pair<>(new byte[0], 0);
    }

    private static Pair<Integer, Integer> parseHexColonHex(String s) {
        int colon = s.indexOf(':');
        if (colon > 0 && colon < s.length()) {
            int first = ParseUtil.hexStringToInt(s.substring(0, colon), 0);
            int second = ParseUtil.hexStringToInt(s.substring(colon + 1), 0);
            return new Pair<>(first, second);
        }
        return new Pair<>(0, 0);
    }

    private static TcpState stateLookup(int state) {
        switch (state) {
            case 0x01:
                return ESTABLISHED;
            case 0x02:
                return SYN_SENT;
            case 0x03:
                return SYN_RECV;
            case 0x04:
                return FIN_WAIT_1;
            case 0x05:
                return FIN_WAIT_2;
            case 0x06:
                return TIME_WAIT;
            case 0x07:
                return CLOSED;
            case 0x08:
                return CLOSE_WAIT;
            case 0x09:
                return LAST_ACK;
            case 0x0A:
                return LISTEN;
            case 0x0B:
                return CLOSING;
            case 0x00:
            default:
                return UNKNOWN;
        }
    }

    @Override public TcpStats getTCPv4Stats() {
        return NetStat.queryTcpStats("netstat -st4");
    }

    @Override public UdpStats getUDPv4Stats() {
        return NetStat.queryUdpStats("netstat -su4");
    }

    @Override public UdpStats getUDPv6Stats() {
        return NetStat.queryUdpStats("netstat -su6");
    }

    @Override public List<IPConnection> getConnections() {
        List<IPConnection> conns = new ArrayList<>();
        Map<Integer, Integer> pidMap = ProcessStat.querySocketToPidMap();
        conns.addAll(queryConnections("tcp", 4, pidMap));
        conns.addAll(queryConnections("tcp", 6, pidMap));
        conns.addAll(queryConnections("udp", 4, pidMap));
        conns.addAll(queryConnections("udp", 6, pidMap));
        return conns;
    }
}
