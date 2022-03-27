
package com.zestic.system.hardware.platform.unix.aix;

import com.sun.jna.Native;
import com.sun.jna.platform.unix.aix.Perfstat.perfstat_disk_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.aix.Ls;
import com.zestic.system.driver.unix.aix.Lscfg;
import com.zestic.system.driver.unix.aix.Lspv;
import com.zestic.system.hardware.HWDiskStore;
import com.zestic.system.hardware.HWPartition;
import com.zestic.system.hardware.common.AbstractHWDiskStore;
import com.zestic.system.util.Constants;
import com.zestic.system.util.tuples.Pair;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/*
 * AIX hard disk implementation.
 */
@ThreadSafe public final class AixHWDiskStore extends AbstractHWDiskStore {

    private final Supplier<perfstat_disk_t[]> diskStats;

    private long reads = 0L;
    private long readBytes = 0L;
    private long writes = 0L;
    private long writeBytes = 0L;
    private long currentQueueLength = 0L;
    private long transferTime = 0L;
    private long timeStamp = 0L;
    private List<HWPartition> partitionList;

    private AixHWDiskStore(String name, String model, String serial, long size,
        Supplier<perfstat_disk_t[]> diskStats) {
        super(name, model, serial, size);
        this.diskStats = diskStats;
    }

    /*
     * Gets the disks on this machine
     *
     * @param diskStats Memoized supplier of disk statistics
     * @return a list of {@link HWDiskStore} objects representing the disks
     */
    public static List<HWDiskStore> getDisks(Supplier<perfstat_disk_t[]> diskStats) {
        Map<String, Pair<Integer, Integer>> majMinMap = Ls.queryDeviceMajorMinor();
        List<AixHWDiskStore> storeList = new ArrayList<>();
        for (perfstat_disk_t disk : diskStats.get()) {
            String storeName = Native.toString(disk.name);
            Pair<String, String> ms = Lscfg.queryModelSerial(storeName);
            String model = ms.getA() == null ? Native.toString(disk.description) : ms.getA();
            String serial = ms.getB() == null ? Constants.UNKNOWN : ms.getB();
            storeList.add(
                createStore(storeName, model, serial, disk.size << 20, diskStats, majMinMap));
        }
        return storeList.stream().sorted(Comparator.comparingInt(s -> s.getPartitions().isEmpty() ?
            Integer.MAX_VALUE :
            s.getPartitions().get(0).getMajor())).collect(Collectors.toList());
    }

    private static AixHWDiskStore createStore(String diskName, String model, String serial,
        long size, Supplier<perfstat_disk_t[]> diskStats,
        Map<String, Pair<Integer, Integer>> majMinMap) {
        AixHWDiskStore store =
            new AixHWDiskStore(diskName, model.isEmpty() ? Constants.UNKNOWN : model, serial, size,
                diskStats);
        store.partitionList = Collections.unmodifiableList(
            Lspv.queryLogicalVolumes(diskName, majMinMap).stream().sorted(
                    Comparator.comparing(HWPartition::getMinor).thenComparing(HWPartition::getName))
                .collect(Collectors.toList()));
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
        for (perfstat_disk_t stat : diskStats.get()) {
            String name = Native.toString(stat.name);
            if (name.equals(this.getName())) {
                // we only have total transfers so estimate read/write ratio from blocks
                long blks = stat.rblks + stat.wblks;
                this.reads = stat.xfers;
                if (blks > 0L) {
                    this.writes = stat.xfers * stat.wblks / blks;
                    this.reads -= this.writes;
                }
                this.readBytes = stat.rblks * stat.bsize;
                this.writeBytes = stat.wblks * stat.bsize;
                this.currentQueueLength = stat.qdepth;
                this.transferTime = stat.time;
                return true;
            }
        }
        return false;
    }
}
