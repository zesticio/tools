
package com.zestic.system.hardware.platform.unix.solaris;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import com.sun.jna.platform.unix.solaris.LibKstat.KstatIO;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.solaris.disk.Iostat;
import com.zestic.system.driver.unix.solaris.disk.Lshal;
import com.zestic.system.driver.unix.solaris.disk.Prtvtoc;
import com.zestic.system.hardware.HWDiskStore;
import com.zestic.system.hardware.HWPartition;
import com.zestic.system.hardware.common.AbstractHWDiskStore;
import com.zestic.system.util.platform.unix.solaris.KstatUtil;
import com.zestic.system.util.tuples.Quintet;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/*
 * Solaris hard disk implementation.
 */
@ThreadSafe public final class SolarisHWDiskStore extends AbstractHWDiskStore {

    private long reads = 0L;
    private long readBytes = 0L;
    private long writes = 0L;
    private long writeBytes = 0L;
    private long currentQueueLength = 0L;
    private long transferTime = 0L;
    private long timeStamp = 0L;
    private List<HWPartition> partitionList;

    private SolarisHWDiskStore(String name, String model, String serial, long size) {
        super(name, model, serial, size);
    }

    /*
     * Gets the disks on this machine
     *
     * @return a list of {@link HWDiskStore} objects representing the disks
     */
    public static List<HWDiskStore> getDisks() {
        // Create map to correlate disk name with block device mount point for
        // later use in partition info
        Map<String, String> deviceMap = Iostat.queryPartitionToMountMap();

        // Create map to correlate disk name with block device mount point for
        // later use in partition info. Run lshal, if available, to get block device
        // major (we'll use partition # for minor)
        Map<String, Integer> majorMap = Lshal.queryDiskToMajorMap();

        // Create map of model, vendor, product, serial, size
        // We'll use Model if available, otherwise Vendor+Product
        Map<String, Quintet<String, String, String, String, Long>> deviceStringMap =
            Iostat.queryDeviceStrings(deviceMap.keySet());

        List<HWDiskStore> storeList = new ArrayList<>();
        for (Entry<String, Quintet<String, String, String, String, Long>> entry : deviceStringMap.entrySet()) {
            String storeName = entry.getKey();
            Quintet<String, String, String, String, Long> val = entry.getValue();
            storeList.add(
                createStore(storeName, val.getA(), val.getB(), val.getC(), val.getD(), val.getE(),
                    deviceMap.getOrDefault(storeName, ""), majorMap.getOrDefault(storeName, 0)));
        }

        return storeList;
    }

    private static SolarisHWDiskStore createStore(String diskName, String model, String vendor,
        String product, String serial, long size, String mount, int major) {
        SolarisHWDiskStore store = new SolarisHWDiskStore(diskName,
            model.isEmpty() ? (vendor + " " + product).trim() : model, serial, size);
        store.partitionList = Collections.unmodifiableList(
            Prtvtoc.queryPartitions(mount, major).stream()
                .sorted(Comparator.comparing(HWPartition::getName)).collect(Collectors.toList()));
        store.updateAttributes();
        return store;
    }

    @Override public long getReads() {
        return reads;
    }

    @Override public long getReadBytes() {
        return readBytes;
    }

    @Override public long getWrites() {
        return writes;
    }

    @Override public long getWriteBytes() {
        return writeBytes;
    }

    @Override public long getCurrentQueueLength() {
        return currentQueueLength;
    }

    @Override public long getTransferTime() {
        return transferTime;
    }

    @Override public long getTimeStamp() {
        return timeStamp;
    }

    @Override public List<HWPartition> getPartitions() {
        return this.partitionList;
    }

    @Override public boolean updateAttributes() {
        try (KstatUtil.KstatChain kc = KstatUtil.openChain()) {
            Kstat ksp = KstatUtil.KstatChain.lookup(null, 0, getName());
            if (ksp != null && KstatUtil.KstatChain.read(ksp)) {
                KstatIO data = new KstatIO(ksp.ks_data);
                this.reads = data.reads;
                this.writes = data.writes;
                this.readBytes = data.nread;
                this.writeBytes = data.nwritten;
                this.currentQueueLength = (long) data.wcnt + data.rcnt;
                // rtime and snaptime are nanoseconds, convert to millis
                this.transferTime = data.rtime / 1_000_000L;
                this.timeStamp = ksp.ks_snaptime / 1_000_000L;
                return true;
            }
        }
        return false;
    }
}
