
package com.zestic.system.software.os.unix.freebsd;

import com.sun.jna.Memory;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.NetStat;
import com.zestic.system.jna.platform.unix.CLibrary;
import com.zestic.system.software.common.AbstractInternetProtocolStats;
import com.zestic.system.util.Memoizer;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.unix.freebsd.BsdSysctlUtil;
import com.zestic.system.util.tuples.Pair;

import java.util.function.Supplier;

/*
 * Internet Protocol Stats implementation
 */
@ThreadSafe public class FreeBsdInternetProtocolStats extends AbstractInternetProtocolStats {

    private Supplier<Pair<Long, Long>> establishedv4v6 =
        Memoizer.memoize(NetStat::queryTcpnetstat, Memoizer.defaultExpiration());
    private Supplier<CLibrary.BsdTcpstat> tcpstat =
        Memoizer.memoize(FreeBsdInternetProtocolStats::queryTcpstat, Memoizer.defaultExpiration());
    private Supplier<CLibrary.BsdUdpstat> udpstat =
        Memoizer.memoize(FreeBsdInternetProtocolStats::queryUdpstat, Memoizer.defaultExpiration());

    private static CLibrary.BsdTcpstat queryTcpstat() {
        CLibrary.BsdTcpstat ft = new CLibrary.BsdTcpstat();
        Memory m = BsdSysctlUtil.sysctl("net.inet.tcp.stats");
        if (m != null && m.size() >= 128) {
            ft.tcps_connattempt = m.getInt(0);
            ft.tcps_accepts = m.getInt(4);
            ft.tcps_drops = m.getInt(12);
            ft.tcps_conndrops = m.getInt(16);
            ft.tcps_sndpack = m.getInt(64);
            ft.tcps_sndrexmitpack = m.getInt(72);
            ft.tcps_rcvpack = m.getInt(104);
            ft.tcps_rcvbadsum = m.getInt(112);
            ft.tcps_rcvbadoff = m.getInt(116);
            ft.tcps_rcvmemdrop = m.getInt(120);
            ft.tcps_rcvshort = m.getInt(124);
        }
        return ft;
    }

    private static CLibrary.BsdUdpstat queryUdpstat() {
        CLibrary.BsdUdpstat ut = new CLibrary.BsdUdpstat();
        Memory m = BsdSysctlUtil.sysctl("net.inet.udp.stats");
        if (m != null && m.size() >= 1644) {
            ut.udps_ipackets = m.getInt(0);
            ut.udps_hdrops = m.getInt(4);
            ut.udps_badsum = m.getInt(8);
            ut.udps_badlen = m.getInt(12);
            ut.udps_opackets = m.getInt(36);
            ut.udps_noportmcast = m.getInt(48);
            ut.udps_rcv6_swcsum = m.getInt(64);
            ut.udps_snd6_swcsum = m.getInt(80);
        }
        return ut;
    }

    @Override public TcpStats getTCPv4Stats() {
        CLibrary.BsdTcpstat tcp = tcpstat.get();
        return new TcpStats(establishedv4v6.get().getA(),
            ParseUtil.unsignedIntToLong(tcp.tcps_connattempt),
            ParseUtil.unsignedIntToLong(tcp.tcps_accepts),
            ParseUtil.unsignedIntToLong(tcp.tcps_conndrops),
            ParseUtil.unsignedIntToLong(tcp.tcps_drops),
            ParseUtil.unsignedIntToLong(tcp.tcps_sndpack),
            ParseUtil.unsignedIntToLong(tcp.tcps_rcvpack),
            ParseUtil.unsignedIntToLong(tcp.tcps_sndrexmitpack), ParseUtil.unsignedIntToLong(
            tcp.tcps_rcvbadsum + tcp.tcps_rcvbadoff + tcp.tcps_rcvmemdrop + tcp.tcps_rcvshort), 0L);
    }

    @Override public UdpStats getUDPv4Stats() {
        CLibrary.BsdUdpstat stat = udpstat.get();
        return new UdpStats(ParseUtil.unsignedIntToLong(stat.udps_opackets),
            ParseUtil.unsignedIntToLong(stat.udps_ipackets),
            ParseUtil.unsignedIntToLong(stat.udps_noportmcast),
            ParseUtil.unsignedIntToLong(stat.udps_hdrops + stat.udps_badsum + stat.udps_badlen));
    }

    @Override public UdpStats getUDPv6Stats() {
        CLibrary.BsdUdpstat stat = udpstat.get();
        return new UdpStats(ParseUtil.unsignedIntToLong(stat.udps_snd6_swcsum),
            ParseUtil.unsignedIntToLong(stat.udps_rcv6_swcsum), 0L, 0L);
    }
}
