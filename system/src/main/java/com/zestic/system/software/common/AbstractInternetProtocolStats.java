
package com.zestic.system.software.common;

import com.zestic.system.driver.unix.NetStat;
import com.zestic.system.software.os.InternetProtocolStats;

import java.util.List;

/*
 * Common implementations for IP Stats
 */
public abstract class AbstractInternetProtocolStats implements InternetProtocolStats {

    public AbstractInternetProtocolStats() {
        super();
    }

    @Override public TcpStats getTCPv6Stats() {
        // Default when OS doesn't have separate TCPv6 stats
        return new TcpStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    }

    @Override public UdpStats getUDPv6Stats() {
        // Default when OS doesn't have separate UDPv6 stats
        return new UdpStats(0L, 0L, 0L, 0L);
    }

    @Override public List<IPConnection> getConnections() {
        return NetStat.queryNetstat();
    }
}
