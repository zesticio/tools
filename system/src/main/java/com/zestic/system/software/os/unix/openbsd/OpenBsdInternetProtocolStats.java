
package com.zestic.system.software.os.unix.openbsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.NetStat;
import com.zestic.system.software.common.AbstractInternetProtocolStats;

/*
 * Internet Protocol Stats implementation
 */
@ThreadSafe public class OpenBsdInternetProtocolStats extends AbstractInternetProtocolStats {

    @Override public TcpStats getTCPv4Stats() {
        return NetStat.queryTcpStats("netstat -s -p tcp");
    }

    @Override public UdpStats getUDPv4Stats() {
        return NetStat.queryUdpStats("netstat -s -p udp");
    }
}
