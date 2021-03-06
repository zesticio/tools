
package com.zestic.system.hardware.platform.unix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.NetworkIF;
import com.zestic.system.hardware.common.AbstractNetworkIF;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

/*
 * BsdNetworkIF applicable to FreeBSD and OpenBSD.
 */
@ThreadSafe
public final class BsdNetworkIF extends AbstractNetworkIF {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);

    private long bytesRecv;
    private long bytesSent;
    private long packetsRecv;
    private long packetsSent;
    private long inErrors;
    private long outErrors;
    private long inDrops;
    private long collisions;
    private long timeStamp;

    public BsdNetworkIF(NetworkInterface netint) throws InstantiationException {
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
                ifList.add(new BsdNetworkIF(ni));
            } catch (InstantiationException e) {
                LOG.debug("Network Interface Instantiation failed: {" + e.getMessage() + "}");
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
        return 0;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public boolean updateAttributes() {
        String stats = ExecutingCommand.getAnswerAt("netstat -bI " + getName(), 1);
        this.timeStamp = System.currentTimeMillis();
        String[] split = ParseUtil.whitespaces.split(stats);
        if (split.length < 12) {
            // No update
            return false;
        }
        this.bytesSent = ParseUtil.parseUnsignedLongOrDefault(split[10], 0L);
        this.bytesRecv = ParseUtil.parseUnsignedLongOrDefault(split[7], 0L);
        this.packetsSent = ParseUtil.parseUnsignedLongOrDefault(split[8], 0L);
        this.packetsRecv = ParseUtil.parseUnsignedLongOrDefault(split[4], 0L);
        this.outErrors = ParseUtil.parseUnsignedLongOrDefault(split[9], 0L);
        this.inErrors = ParseUtil.parseUnsignedLongOrDefault(split[5], 0L);
        this.collisions = ParseUtil.parseUnsignedLongOrDefault(split[11], 0L);
        this.inDrops = ParseUtil.parseUnsignedLongOrDefault(split[6], 0L);
        return true;
    }
}
