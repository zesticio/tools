
package com.zestic.system.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.NetworkIF;
import com.zestic.system.hardware.common.AbstractNetworkIF;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.util.platform.unix.solaris.KstatUtil;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

/*
 * SolarisNetworks class.
 */
@ThreadSafe
public final class SolarisNetworkIF extends AbstractNetworkIF {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);

    private long bytesRecv;
    private long bytesSent;
    private long packetsRecv;
    private long packetsSent;
    private long inErrors;
    private long outErrors;
    private long inDrops;
    private long collisions;
    private long speed;
    private long timeStamp;

    public SolarisNetworkIF(NetworkInterface netint) throws InstantiationException {
        super(netint);
        updateAttributes();
    }

    /*
     * Gets all network interfaces on this machine
     *
     * @param includeLocalInterfaces include local interfaces in the result
     * @return A list of {@link NetworkIF} objects representing the interfaces
     */
    public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
        List<NetworkIF> ifList = new ArrayList<>();
        for (NetworkInterface ni : getNetworkInterfaces(includeLocalInterfaces)) {
            try {
                ifList.add(new SolarisNetworkIF(ni));
            } catch (InstantiationException e) {
                LOG.debug("Network Interface Instantiation failed: {}" + e.getMessage());
            }
        }
        return ifList;
    }

    @Override
    public long getBytesRecv() {
        return this.bytesRecv;
    }

    @Override
    public long getBytesSent() {
        return this.bytesSent;
    }

    @Override
    public long getPacketsRecv() {
        return this.packetsRecv;
    }

    @Override
    public long getPacketsSent() {
        return this.packetsSent;
    }

    @Override
    public long getInErrors() {
        return this.inErrors;
    }

    @Override
    public long getOutErrors() {
        return this.outErrors;
    }

    @Override
    public long getInDrops() {
        return this.inDrops;
    }

    @Override
    public long getCollisions() {
        return this.collisions;
    }

    @Override
    public long getSpeed() {
        return this.speed;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public boolean updateAttributes() {
        try (KstatUtil.KstatChain kc = KstatUtil.openChain()) {
            Kstat ksp = KstatUtil.KstatChain.lookup("link", -1, getName());
            if (ksp == null) { // Solaris 10 compatibility
                ksp = KstatUtil.KstatChain.lookup(null, -1, getName());
            }
            if (ksp != null && KstatUtil.KstatChain.read(ksp)) {
                this.bytesSent = KstatUtil.dataLookupLong(ksp, "obytes64");
                this.bytesRecv = KstatUtil.dataLookupLong(ksp, "rbytes64");
                this.packetsSent = KstatUtil.dataLookupLong(ksp, "opackets64");
                this.packetsRecv = KstatUtil.dataLookupLong(ksp, "ipackets64");
                this.outErrors = KstatUtil.dataLookupLong(ksp, "oerrors");
                this.inErrors = KstatUtil.dataLookupLong(ksp, "ierrors");
                this.collisions = KstatUtil.dataLookupLong(ksp, "collisions");
                this.inDrops = KstatUtil.dataLookupLong(ksp, "dl_idrops");
                this.speed = KstatUtil.dataLookupLong(ksp, "ifspeed");
                // Snap time in ns; convert to ms
                this.timeStamp = ksp.ks_snaptime / 1_000_000L;
                return true;
            }
        }
        return false;
    }
}
