
package com.zestic.system.software.os.windows;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractOSFileStore;
import com.zestic.system.software.os.OSFileStore;

import java.util.List;

/*
 * OSFileStore implementation
 */
@ThreadSafe public class WindowsOSFileStore extends AbstractOSFileStore {

    private String logicalVolume;
    private String description;
    private String fsType;

    private long freeSpace;
    private long usableSpace;
    private long totalSpace;
    private long freeInodes;
    private long totalInodes;

    public WindowsOSFileStore(String name, String volume, String label, String mount,
        String options, String uuid, String logicalVolume, String description, String fsType,
        long freeSpace, long usableSpace, long totalSpace, long freeInodes, long totalInodes) {
        super(name, volume, label, mount, options, uuid);
        this.logicalVolume = logicalVolume;
        this.description = description;
        this.fsType = fsType;
        this.freeSpace = freeSpace;
        this.usableSpace = usableSpace;
        this.totalSpace = totalSpace;
        this.freeInodes = freeInodes;
        this.totalInodes = totalInodes;
    }

    @Override public String getLogicalVolume() {
        return this.logicalVolume;
    }

    @Override public String getDescription() {
        return this.description;
    }

    @Override public String getType() {
        return this.fsType;
    }

    @Override public long getFreeSpace() {
        return this.freeSpace;
    }

    @Override public long getUsableSpace() {
        return this.usableSpace;
    }

    @Override public long getTotalSpace() {
        return this.totalSpace;
    }

    @Override public long getFreeInodes() {
        return this.freeInodes;
    }

    @Override public long getTotalInodes() {
        return this.totalInodes;
    }

    @Override public boolean updateAttributes() {
        // Check if we have the volume locally
        List<OSFileStore> volumes = WindowsFileSystem.getLocalVolumes(getVolume());
        if (volumes.isEmpty()) {
            // Not locally, search WMI
            String nameToMatch = getMount().length() < 2 ? null : getMount().substring(0, 2);
            volumes = WindowsFileSystem.getWmiVolumes(nameToMatch, false);
        }
        for (OSFileStore fileStore : volumes) {
            if (getVolume().equals(fileStore.getVolume()) && getMount().equals(
                fileStore.getMount())) {
                this.logicalVolume = fileStore.getLogicalVolume();
                this.description = fileStore.getDescription();
                this.fsType = fileStore.getType();
                this.freeSpace = fileStore.getFreeSpace();
                this.usableSpace = fileStore.getUsableSpace();
                this.totalSpace = fileStore.getTotalSpace();
                this.freeInodes = fileStore.getFreeInodes();
                this.totalInodes = fileStore.getTotalInodes();
                return true;
            }
        }
        return false;
    }
}
