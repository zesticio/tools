
package com.zestic.system.software.os.unix.solaris;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractInternetProtocolStats;
import com.zestic.system.software.os.InternetProtocolStats;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.List;

/*
 * Internet Protocol Stats implementation
 */
@ThreadSafe public class SolarisInternetProtocolStats extends AbstractInternetProtocolStats {

    private static InternetProtocolStats.TcpStats getTcpStats() {
        long connectionsEstablished = 0;
        long connectionsActive = 0;
        long connectionsPassive = 0;
        long connectionFailures = 0;
        long connectionsReset = 0;
        long segmentsSent = 0;
        long segmentsReceived = 0;
        long segmentsRetransmitted = 0;
        long inErrors = 0;
        long outResets = 0;
        List<String> netstat = ExecutingCommand.runNative("netstat -s -P tcp");
        // append IP
        netstat.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
        for (String s : netstat) {
            // Two stats per line. Split the strings by index of "tcp"
            String[] stats = splitOnPrefix(s, "tcp");
            // Now of form tcpXX = 123
            for (String stat : stats) {
                if (stat != null) {
                    String[] split = stat.split("=");
                    if (split.length == 2) {
                        switch (split[0].trim()) {
                            case "tcpCurrEstab":
                                connectionsEstablished =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpActiveOpens":
                                connectionsActive =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpPassiveOpens":
                                connectionsPassive =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpAttemptFails":
                                connectionFailures =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpEstabResets":
                                connectionsReset =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpOutSegs":
                                segmentsSent = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpInSegs":
                                segmentsReceived =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpRetransSegs":
                                segmentsRetransmitted =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "tcpInErr":
                                // doesn't have tcp in second column
                                inErrors = ParseUtil.getFirstIntValue(split[1].trim());
                                break;
                            case "tcpOutRsts":
                                outResets = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return new InternetProtocolStats.TcpStats(connectionsEstablished, connectionsActive,
            connectionsPassive, connectionFailures, connectionsReset, segmentsSent,
            segmentsReceived, segmentsRetransmitted, inErrors, outResets);
    }

    private static InternetProtocolStats.UdpStats getUdpStats() {
        long datagramsSent = 0;
        long datagramsReceived = 0;
        long datagramsNoPort = 0;
        long datagramsReceivedErrors = 0;
        List<String> netstat = ExecutingCommand.runNative("netstat -s -P udp");
        // append IP
        netstat.addAll(ExecutingCommand.runNative("netstat -s -P ip"));
        for (String s : netstat) {
            // Two stats per line. Split the strings by index of "udp"
            String[] stats = splitOnPrefix(s, "udp");
            // Now of form udpXX = 123
            for (String stat : stats) {
                if (stat != null) {
                    String[] split = stat.split("=");
                    if (split.length == 2) {
                        switch (split[0].trim()) {
                            case "udpOutDatagrams":
                                datagramsSent = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "udpInDatagrams":
                                datagramsReceived =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "udpNoPorts":
                                datagramsNoPort = ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            case "udpInErrors":
                                datagramsReceivedErrors =
                                    ParseUtil.parseLongOrDefault(split[1].trim(), 0L);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
        }
        return new InternetProtocolStats.UdpStats(datagramsSent, datagramsReceived, datagramsNoPort,
            datagramsReceivedErrors);
    }

    private static String[] splitOnPrefix(String s, String prefix) {
        String[] stats = new String[2];
        int first = s.indexOf(prefix);
        if (first >= 0) {
            int second = s.indexOf(prefix, first + 1);
            if (second >= 0) {
                stats[0] = s.substring(first, second).trim();
                stats[1] = s.substring(second).trim();
            } else {
                stats[0] = s.substring(first).trim();
            }
        }
        return stats;
    }

    @Override public InternetProtocolStats.TcpStats getTCPv4Stats() {
        return getTcpStats();
    }

    @Override public InternetProtocolStats.UdpStats getUDPv4Stats() {
        return getUdpStats();
    }
}
