
package com.zestic.system.hardware.platform.unix.freebsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.freebsd.disk.GeomDiskList;
import com.zestic.system.driver.unix.freebsd.disk.GeomPartList;
import com.zestic.system.hardware.HWDiskStore;
import com.zestic.system.hardware.HWPartition;
import com.zestic.system.hardware.common.AbstractHWDiskStore;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.unix.freebsd.BsdSysctlUtil;
import com.zestic.system.util.tuples.Triplet;

import java.util.*;
import java.util.stream.Collectors;

/*
 * FreeBSD hard disk implementation.
 */
@ThreadSafe public final class FreeBsdHWDiskStore extends AbstractHWDiskStore {

    private long reads = 0L;
    private long readBytes = 0L;
    private long writes = 0L;
    private long writeBytes = 0L;
    private long currentQueueLength = 0L;
    private long transferTime = 0L;
    private long timeStamp = 0L;
    private List<HWPartition> partitionList;

    private FreeBsdHWDiskStore(String name, String model, String serial, long size) {
        super(name, model, serial, size);
    }

    /*
     * Gets the disks on this machine
     *
     * @return a list of {@link HWDiskStore} objects representing the disks
     */
    public static List<HWDiskStore> getDisks() {
        // Result
        List<HWDiskStore> diskList = new ArrayList<>();

        // Get map of disk names to partitions
        Map<String, List<HWPartition>> partitionMap = GeomPartList.queryPartitions();

        // Get map of disk names to disk info
        Map<String, Triplet<String, String, Long>> diskInfoMap = GeomDiskList.queryDisks();

        // Get list of disks from sysctl
        List<String> devices =
            Arrays.asList(ParseUtil.whitespaces.split(BsdSysctlUtil.sysctl("kern.disks", "")));

        // Run iostat -Ix to enumerate disks by name and get kb r/w
        List<String> iostat = ExecutingCommand.runNative("iostat -Ix");
        long now = System.currentTimeMillis();
        for (String line : iostat) {
            String[] split = ParseUtil.whitespaces.split(line);
            if (split.length > 6 && devices.contains(split[0])) {
                Triplet<String, String, Long> storeInfo = diskInfoMap.get(split[0]);
                FreeBsdHWDiskStore store = (storeInfo == null) ?
                    new FreeBsdHWDiskStore(split[0], Constants.UNKNOWN, Constants.UNKNOWN, 0L) :
                    new FreeBsdHWDiskStore(split[0], storeInfo.getA(), storeInfo.getB(),
                        storeInfo.getC());
                store.reads = (long) ParseUtil.parseDoubleOrDefault(split[1], 0d);
                store.writes = (long) ParseUtil.parseDoubleOrDefault(split[2], 0d);
                // In KB
                store.readBytes = (long) (ParseUtil.parseDoubleOrDefault(split[3], 0d) * 1024);
                store.writeBytes = (long) (ParseUtil.parseDoubleOrDefault(split[4], 0d) * 1024);
                // # transactions
                store.currentQueueLength = ParseUtil.parseLongOrDefault(split[5], 0L);
                // In seconds, multiply for ms
                store.transferTime = (long) (ParseUtil.parseDoubleOrDefault(split[6], 0d) * 1000);
                store.partitionList = Collections.unmodifiableList(
                    partitionMap.getOrDefault(split[0], Collections.emptyList()).stream()
                        .sorted(Comparator.comparing(HWPartition::getName))
                        .collect(Collectors.toList()));
                store.timeStamp = now;
                diskList.add(store);
            }
        }
        return diskList;
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
        List<String> output = ExecutingCommand.runNative("iostat -Ix " + getName());
        long now = System.currentTimeMillis();
        boolean diskFound = false;
        for (String line : output) {
            String[] split = ParseUtil.whitespaces.split(line);
            if (split.length < 7 || !split[0].equals(getName())) {
                continue;
            }
            diskFound = true;
            this.reads = (long) ParseUtil.parseDoubleOrDefault(split[1], 0d);
            this.writes = (long) ParseUtil.parseDoubleOrDefault(split[2], 0d);
            // In KB
            this.readBytes = (long) (ParseUtil.parseDoubleOrDefault(split[3], 0d) * 1024);
            this.writeBytes = (long) (ParseUtil.parseDoubleOrDefault(split[4], 0d) * 1024);
            // # transactions
            this.currentQueueLength = ParseUtil.parseLongOrDefault(split[5], 0L);
            // In seconds, multiply for ms
            this.transferTime = (long) (ParseUtil.parseDoubleOrDefault(split[6], 0d) * 1000);
            this.timeStamp = now;
        }
        return diskFound;
    }
}
