
package com.zestic.system.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation.CFArrayRef;
import com.sun.jna.platform.mac.CoreFoundation.CFStringRef;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.mac.net.NetStat;
import com.zestic.system.driver.mac.net.NetStat.IFdata;
import com.zestic.system.hardware.NetworkIF;
import com.zestic.system.hardware.common.AbstractNetworkIF;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.jna.platform.mac.SystemConfiguration;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * MacNetworks class.
 */
@ThreadSafe
public final class MacNetworkIF extends AbstractNetworkIF {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.LogManager.getLogger(AixNetworkIF.class);

    private int ifType;
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

    public MacNetworkIF(NetworkInterface netint, Map<Integer, IFdata> data)
            throws InstantiationException {
        super(netint, queryIfDisplayName(netint));
        updateNetworkStats(data);
    }

    private static String queryIfDisplayName(NetworkInterface netint) {
        String name = netint.getName();
        CFArrayRef ifArray = SystemConfiguration.INSTANCE.SCNetworkInterfaceCopyAll();
        if (ifArray != null) {
            try {
                int count = ifArray.getCount();
                for (int i = 0; i < count; i++) {
                    Pointer pNetIf = ifArray.getValueAtIndex(i);
                    SystemConfiguration.SCNetworkInterfaceRef scNetIf =
                            new SystemConfiguration.SCNetworkInterfaceRef(pNetIf);
                    CFStringRef cfName =
                            SystemConfiguration.INSTANCE.SCNetworkInterfaceGetBSDName(scNetIf);
                    if (cfName != null && name.equals(cfName.stringValue())) {
                        CFStringRef cfDisplayName =
                                SystemConfiguration.INSTANCE.SCNetworkInterfaceGetLocalizedDisplayName(
                                        scNetIf);
                        return cfDisplayName.stringValue();
                    }
                }
            } finally {
                ifArray.release();
            }
        }
        return name;
    }

    /*
     * Gets all network interfaces on this machine
     *
     * @param includeLocalInterfaces include local interfaces in the result
     * @return A list of {@link NetworkIF} objects representing the interfaces
     */
    public static List<NetworkIF> getNetworks(boolean includeLocalInterfaces) {
        // One time fetch of stats
        final Map<Integer, IFdata> data = NetStat.queryIFdata(-1);
        List<NetworkIF> ifList = new ArrayList<>();
        for (NetworkInterface ni : getNetworkInterfaces(includeLocalInterfaces)) {
            try {
                ifList.add(new MacNetworkIF(ni, data));
            } catch (InstantiationException e) {
                LOG.debug("Network Interface Instantiation failed: {" + e.getMessage() + "}");
            }
        }
        return ifList;
    }

    @Override
    public int getIfType() {
        return this.ifType;
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
        int index = queryNetworkInterface().getIndex();
        return updateNetworkStats(NetStat.queryIFdata(index));
    }

    /*
     * Updates interface network statistics on the given interface. Statistics
     * include packets and bytes sent and received, and interface speed.
     *
     * @param data A map of network interface statistics with the index as the key
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    private boolean updateNetworkStats(Map<Integer, IFdata> data) {
        int index = queryNetworkInterface().getIndex();
        if (data.containsKey(index)) {
            IFdata ifData = data.get(index);
            // Update data
            this.ifType = ifData.getIfType();
            this.bytesSent = ifData.getOBytes();
            this.bytesRecv = ifData.getIBytes();
            this.packetsSent = ifData.getOPackets();
            this.packetsRecv = ifData.getIPackets();
            this.outErrors = ifData.getOErrors();
            this.inErrors = ifData.getIErrors();
            this.collisions = ifData.getCollisions();
            this.inDrops = ifData.getIDrops();
            this.speed = ifData.getSpeed();
            this.timeStamp = ifData.getTimeStamp();
            return true;
        }
        return false;
    }

}
