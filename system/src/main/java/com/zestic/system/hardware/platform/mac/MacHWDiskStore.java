
package com.zestic.system.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.CoreFoundation.*;
import com.sun.jna.platform.mac.DiskArbitration;
import com.sun.jna.platform.mac.DiskArbitration.DADiskRef;
import com.sun.jna.platform.mac.DiskArbitration.DASessionRef;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKitUtil;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.mac.disk.Fsstat;
import com.zestic.system.hardware.HWDiskStore;
import com.zestic.system.hardware.HWPartition;
import com.zestic.system.hardware.common.AbstractHWDiskStore;
import com.zestic.system.util.Constants;
import com.zestic.system.util.platform.mac.CFUtil;

import java.util.*;
import java.util.stream.Collectors;

/*
 * Mac hard disk implementation.
 */
@ThreadSafe
public final class MacHWDiskStore extends AbstractHWDiskStore {

    private static final CoreFoundation CF = CoreFoundation.INSTANCE;
    private static final DiskArbitration DA = DiskArbitration.INSTANCE;

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(MacHWDiskStore.class);

    private long reads = 0L;
    private long readBytes = 0L;
    private long writes = 0L;
    private long writeBytes = 0L;
    private long currentQueueLength = 0L;
    private long transferTime = 0L;
    private long timeStamp = 0L;
    private List<HWPartition> partitionList;

    private MacHWDiskStore(String name, String model, String serial, long size,
                           DASessionRef session, Map<String, String> mountPointMap, Map<CFKey, CFStringRef> cfKeyMap) {
        super(name, model, serial, size);
        updateDiskStats(session, mountPointMap, cfKeyMap);
    }

    /*
     * Gets the disks on this machine
     *
     * @return a list of {@link HWDiskStore} objects representing the disks
     */
    public static List<HWDiskStore> getDisks() {
        Map<String, String> mountPointMap = Fsstat.queryPartitionToMountMap();
        Map<CFKey, CFStringRef> cfKeyMap = mapCFKeys();

        List<HWDiskStore> diskList = new ArrayList<>();

        // Open a DiskArbitration session
        DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
        if (session == null) {
            LOG.error("Unable to open session to DiskArbitration framework.");
            return Collections.emptyList();
        }

        // Get IOMedia objects representing whole drives
        List<String> bsdNames = new ArrayList<>();
        IOIterator iter = IOKitUtil.getMatchingServices("IOMedia");
        if (iter != null) {
            IORegistryEntry media = iter.next();
            while (media != null) {
                Boolean whole = media.getBooleanProperty("Whole");
                if (whole != null && whole) {
                    DADiskRef disk =
                            DA.DADiskCreateFromIOMedia(CF.CFAllocatorGetDefault(), session, media);
                    bsdNames.add(DA.DADiskGetBSDName(disk));
                    disk.release();
                }
                media.release();
                media = iter.next();
            }
            iter.release();
        }

        // Now iterate the bsdNames
        for (String bsdName : bsdNames) {
            String model = "";
            String serial = "";
            long size = 0L;

            // Get a reference to the disk - only matching /dev/disk*
            String path = "/dev/" + bsdName;

            // Get the DiskArbitration dictionary for this disk, which has model
            // and size (capacity)
            DADiskRef disk = DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session, path);
            if (disk != null) {
                CFDictionaryRef diskInfo = DA.DADiskCopyDescription(disk);
                if (diskInfo != null) {
                    // Parse out model and size from their respective keys
                    Pointer result = diskInfo.getValue(cfKeyMap.get(CFKey.DA_DEVICE_MODEL));
                    model = CFUtil.cfPointerToString(result);
                    result = diskInfo.getValue(cfKeyMap.get(CFKey.DA_MEDIA_SIZE));
                    CFNumberRef sizePtr = new CFNumberRef(result);
                    size = sizePtr.longValue();
                    diskInfo.release();

                    // Use the model as a key to get serial from IOKit
                    if (!"Disk Image".equals(model)) {
                        CFStringRef modelNameRef = CFStringRef.createCFString(model);
                        CFMutableDictionaryRef propertyDict =
                                CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0),
                                        null, null);
                        propertyDict.setValue(cfKeyMap.get(CFKey.MODEL), modelNameRef);
                        CFMutableDictionaryRef matchingDict =
                                CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0),
                                        null, null);
                        matchingDict.setValue(cfKeyMap.get(CFKey.IO_PROPERTY_MATCH), propertyDict);

                        // search for all IOservices that match the model
                        IOIterator serviceIterator = IOKitUtil.getMatchingServices(matchingDict);
                        // getMatchingServices releases matchingDict
                        modelNameRef.release();
                        propertyDict.release();

                        if (serviceIterator != null) {
                            IORegistryEntry sdService = serviceIterator.next();
                            while (sdService != null) {
                                // look up the serial number
                                serial = sdService.getStringProperty("Serial Number");
                                sdService.release();
                                if (serial != null) {
                                    break;
                                }
                                // iterate
                                sdService.release();
                                sdService = serviceIterator.next();
                            }
                            serviceIterator.release();
                        }
                        if (serial == null) {
                            serial = "";
                        }
                    }
                }
                disk.release();

                // If empty, ignore
                if (size <= 0) {
                    continue;
                }
                HWDiskStore diskStore =
                        new MacHWDiskStore(bsdName, model.trim(), serial.trim(), size, session,
                                mountPointMap, cfKeyMap);
                diskList.add(diskStore);
            }
        }
        // Close DA session
        session.release();
        for (CFTypeRef value : cfKeyMap.values()) {
            value.release();
        }
        return diskList;
    }

    /*
     * Temporarily cache pointers to keys. The values from this map must be released
     * after use.}
     *
     * @return A map of keys in the {@link CFKey} enum to corresponding
     * {@link CFStringRef}.
     */
    private static Map<CFKey, CFStringRef> mapCFKeys() {
        Map<CFKey, CFStringRef> keyMap = new EnumMap<>(CFKey.class);
        for (CFKey cfKey : CFKey.values()) {
            keyMap.put(cfKey, CFStringRef.createCFString(cfKey.getKey()));
        }
        return keyMap;
    }

    @Override
    public long getReads() {
        return reads;
    }

    @Override
    public long getReadBytes() {
        return readBytes;
    }

    @Override
    public long getWrites() {
        return writes;
    }

    @Override
    public long getWriteBytes() {
        return writeBytes;
    }

    @Override
    public long getCurrentQueueLength() {
        return currentQueueLength;
    }

    @Override
    public long getTransferTime() {
        return transferTime;
    }

    @Override
    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public List<HWPartition> getPartitions() {
        return this.partitionList;
    }

    @Override
    public boolean updateAttributes() {
        // Open a session and create CFStrings
        DASessionRef session = DA.DASessionCreate(CF.CFAllocatorGetDefault());
        if (session == null) {
            LOG.error("Unable to open session to DiskArbitration framework.");
            return false;
        }
        Map<CFKey, CFStringRef> cfKeyMap = mapCFKeys();
        // Execute the update
        boolean diskFound = updateDiskStats(session, Fsstat.queryPartitionToMountMap(), cfKeyMap);
        // Release the session and CFStrings
        session.release();
        for (CFTypeRef value : cfKeyMap.values()) {
            value.release();
        }

        return diskFound;
    }

    private boolean updateDiskStats(DASessionRef session, Map<String, String> mountPointMap,
                                    Map<CFKey, CFStringRef> cfKeyMap) {
        // Now look up the device using the BSD Name to get its
        // statistics
        String bsdName = getName();
        CFMutableDictionaryRef matchingDict = IOKitUtil.getBSDNameMatchingDict(bsdName);
        if (matchingDict != null) {
            // search for all IOservices that match the bsd name
            IOIterator driveListIter = IOKitUtil.getMatchingServices(matchingDict);
            if (driveListIter != null) {
                // getMatchingServices releases matchingDict
                IORegistryEntry drive = driveListIter.next();
                // Should only match one drive
                if (drive != null) {
                    // Should be an IOMedia object with a parent
                    // IOBlockStorageDriver or AppleAPFSContainerScheme object
                    // Get the properties from the parent
                    if (drive.conformsTo("IOMedia")) {
                        IORegistryEntry parent = drive.getParentEntry("IOService");
                        if (parent != null && (parent.conformsTo("IOBlockStorageDriver")
                                || parent.conformsTo("AppleAPFSContainerScheme"))) {
                            CFMutableDictionaryRef properties = parent.createCFProperties();
                            // We now have a properties object with the
                            // statistics we need on it. Fetch them
                            Pointer result = properties.getValue(cfKeyMap.get(CFKey.STATISTICS));
                            CFDictionaryRef statistics = new CFDictionaryRef(result);
                            this.timeStamp = System.currentTimeMillis();

                            // Now get the stats we want
                            result = statistics.getValue(cfKeyMap.get(CFKey.READ_OPS));
                            CFNumberRef stat = new CFNumberRef(result);
                            this.reads = stat.longValue();
                            result = statistics.getValue(cfKeyMap.get(CFKey.READ_BYTES));
                            stat.setPointer(result);
                            this.readBytes = stat.longValue();

                            result = statistics.getValue(cfKeyMap.get(CFKey.WRITE_OPS));
                            stat.setPointer(result);
                            this.writes = stat.longValue();
                            result = statistics.getValue(cfKeyMap.get(CFKey.WRITE_BYTES));
                            stat.setPointer(result);
                            this.writeBytes = stat.longValue();

                            // Total time is in nanoseconds. Add read+write
                            // and convert total to ms
                            final Pointer readTimeResult =
                                    statistics.getValue(cfKeyMap.get(CFKey.READ_TIME));
                            final Pointer writeTimeResult =
                                    statistics.getValue(cfKeyMap.get(CFKey.WRITE_TIME));
                            // AppleAPFSContainerScheme does not have timer statistics
                            if (readTimeResult != null && writeTimeResult != null) {
                                stat.setPointer(readTimeResult);
                                long xferTime = stat.longValue();
                                stat.setPointer(writeTimeResult);
                                xferTime += stat.longValue();
                                this.transferTime = xferTime / 1_000_000L;
                            }

                            properties.release();
                        } else {
                            // This is normal for FileVault drives, Fusion
                            // drives, and other virtual bsd names
                            LOG.debug("Unable to find block storage driver properties for {}" +
                                    bsdName);
                        }
                        // Now get partitions for this disk.
                        List<HWPartition> partitions = new ArrayList<>();

                        CFMutableDictionaryRef properties = drive.createCFProperties();
                        // Partitions will match BSD Unit property
                        Pointer result = properties.getValue(cfKeyMap.get(CFKey.BSD_UNIT));
                        CFNumberRef bsdUnit = new CFNumberRef(result);
                        // We need a CFBoolean that's false.
                        // Whole disk has 'true' for Whole and 'false'
                        // for leaf; store the boolean false
                        result = properties.getValue(cfKeyMap.get(CFKey.LEAF));
                        CFBooleanRef cfFalse = new CFBooleanRef(result);
                        // create a matching dict for BSD Unit
                        CFMutableDictionaryRef propertyDict =
                                CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0),
                                        null, null);
                        propertyDict.setValue(cfKeyMap.get(CFKey.BSD_UNIT), bsdUnit);
                        propertyDict.setValue(cfKeyMap.get(CFKey.WHOLE), cfFalse);
                        matchingDict =
                                CF.CFDictionaryCreateMutable(CF.CFAllocatorGetDefault(), new CFIndex(0),
                                        null, null);
                        matchingDict.setValue(cfKeyMap.get(CFKey.IO_PROPERTY_MATCH), propertyDict);

                        // search for IOservices that match the BSD Unit
                        // with whole=false; these are partitions
                        IOIterator serviceIterator = IOKitUtil.getMatchingServices(matchingDict);
                        // getMatchingServices releases matchingDict
                        properties.release();
                        propertyDict.release();

                        if (serviceIterator != null) {
                            // Iterate disks
                            IORegistryEntry sdService =
                                    IOKit.INSTANCE.IOIteratorNext(serviceIterator);
                            while (sdService != null) {
                                // look up the BSD Name
                                String partBsdName = sdService.getStringProperty("BSD Name");
                                String name = partBsdName;
                                String type = "";
                                // Get the DiskArbitration dictionary for
                                // this partition
                                DADiskRef disk =
                                        DA.DADiskCreateFromBSDName(CF.CFAllocatorGetDefault(), session,
                                                partBsdName);
                                if (disk != null) {
                                    CFDictionaryRef diskInfo = DA.DADiskCopyDescription(disk);
                                    if (diskInfo != null) {
                                        // get volume name from its key
                                        result =
                                                diskInfo.getValue(cfKeyMap.get(CFKey.DA_MEDIA_NAME));
                                        type = CFUtil.cfPointerToString(result);
                                        result =
                                                diskInfo.getValue(cfKeyMap.get(CFKey.DA_VOLUME_NAME));
                                        if (result == null) {
                                            name = type;
                                        } else {
                                            name = CFUtil.cfPointerToString(result);
                                        }
                                        diskInfo.release();
                                    }
                                    disk.release();
                                }
                                String mountPoint = mountPointMap.getOrDefault(partBsdName, "");
                                Long size = sdService.getLongProperty("Size");
                                Integer bsdMajor = sdService.getIntegerProperty("BSD Major");
                                Integer bsdMinor = sdService.getIntegerProperty("BSD Minor");
                                String uuid = sdService.getStringProperty("UUID");
                                partitions.add(new HWPartition(partBsdName, name, type,
                                        uuid == null ? Constants.UNKNOWN : uuid,
                                        size == null ? 0L : size, bsdMajor == null ? 0 : bsdMajor,
                                        bsdMinor == null ? 0 : bsdMinor, mountPoint));
                                // iterate
                                sdService.release();
                                sdService = IOKit.INSTANCE.IOIteratorNext(serviceIterator);
                            }
                            serviceIterator.release();
                        }
                        this.partitionList = Collections.unmodifiableList(
                                partitions.stream().sorted(Comparator.comparing(HWPartition::getName))
                                        .collect(Collectors.toList()));
                        if (parent != null) {
                            parent.release();
                        }
                    } else {
                        LOG.error("Unable to find IOMedia device or parent for {}" + bsdName);
                    }
                    drive.release();
                }
                driveListIter.release();
                return true;
            }
        }
        return false;
    }

    /*
     * Strings to convert to CFStringRef for pointer lookups
     */
    private enum CFKey {
        IO_PROPERTY_MATCH("IOPropertyMatch"), //

        STATISTICS("Statistics"), //
        READ_OPS("Operations (Read)"), READ_BYTES("Bytes (Read)"), READ_TIME(
                "Total Time (Read)"), //
        WRITE_OPS("Operations (Write)"), WRITE_BYTES("Bytes (Write)"), WRITE_TIME(
                "Total Time (Write)"), //

        BSD_UNIT("BSD Unit"), LEAF("Leaf"), WHOLE("Whole"), //

        DA_MEDIA_NAME("DAMediaName"), DA_VOLUME_NAME("DAVolumeName"), DA_MEDIA_SIZE(
                "DAMediaSize"), //
        DA_DEVICE_MODEL("DADeviceModel"), MODEL("Model");

        private final String key;

        CFKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return this.key;
        }
    }
}
