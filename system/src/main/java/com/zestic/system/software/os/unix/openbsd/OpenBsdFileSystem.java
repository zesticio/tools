
package com.zestic.system.software.os.unix.openbsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractFileSystem;
import com.zestic.system.software.os.OSFileStore;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.FileSystemUtil;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.platform.unix.openbsd.OpenBsdSysctlUtil;

import java.io.File;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * The FreeBSD File System contains {@link OSFileStore}s which
 * are a storage pool, device, partition, volume, concrete file system or other
 * implementation specific means of file storage.
 */
@ThreadSafe public class OpenBsdFileSystem extends AbstractFileSystem {

    public static final String OSHI_OPENBSD_FS_PATH_EXCLUDES =
        "com.zestic.system.os.openbsd.filesystem.path.excludes";
    public static final String OSHI_OPENBSD_FS_PATH_INCLUDES =
        "com.zestic.system.os.openbsd.filesystem.path.includes";
    public static final String OSHI_OPENBSD_FS_VOLUME_EXCLUDES =
        "com.zestic.system.os.openbsd.filesystem.volume.excludes";
    public static final String OSHI_OPENBSD_FS_VOLUME_INCLUDES =
        "com.zestic.system.os.openbsd.filesystem.volume.includes";

    private static final List<PathMatcher> FS_PATH_EXCLUDES =
        FileSystemUtil.loadAndParseFileSystemConfig(OSHI_OPENBSD_FS_PATH_EXCLUDES);
    private static final List<PathMatcher> FS_PATH_INCLUDES =
        FileSystemUtil.loadAndParseFileSystemConfig(OSHI_OPENBSD_FS_PATH_INCLUDES);
    private static final List<PathMatcher> FS_VOLUME_EXCLUDES =
        FileSystemUtil.loadAndParseFileSystemConfig(OSHI_OPENBSD_FS_VOLUME_EXCLUDES);
    private static final List<PathMatcher> FS_VOLUME_INCLUDES =
        FileSystemUtil.loadAndParseFileSystemConfig(OSHI_OPENBSD_FS_VOLUME_INCLUDES);

    // Called by OpenBsdOSFileStore
    static List<OSFileStore> getFileStoreMatching(String nameToMatch) {
        return getFileStoreMatching(nameToMatch, false);
    }

    private static List<OSFileStore> getFileStoreMatching(String nameToMatch, boolean localOnly) {
        List<OSFileStore> fsList = new ArrayList<>();

        // Get inode usage data
        Map<String, Long> inodeFreeMap = new HashMap<>();
        Map<String, Long> inodeUsedlMap = new HashMap<>();
        String command = "df -i" + (localOnly ? " -l" : "");
        for (String line : ExecutingCommand.runNative(command)) {
            /*- Sample Output:
             $ df -i
            Filesystem  512-blocks      Used     Avail Capacity iused   ifree  %iused  Mounted on
            /dev/wd0a      2149212    908676   1133076    45%    8355  147163     5%   /
            /dev/wd0e      4050876        36   3848300     0%      10  285108     0%   /home
            /dev/wd0d      6082908   3343172   2435592    58%   27813  386905     7%   /usr
            */
            if (line.startsWith("/")) {
                String[] split = ParseUtil.whitespaces.split(line);
                if (split.length > 6) {
                    inodeUsedlMap.put(split[0], ParseUtil.parseLongOrDefault(split[5], 0L));
                    inodeFreeMap.put(split[0], ParseUtil.parseLongOrDefault(split[6], 0L));
                }
            }
        }

        // Get mount table
        for (String fs : ExecutingCommand.runNative("mount -v")) { // NOSONAR squid:S135
            /*-
             Sample Output:
             /dev/wd0a (d1c342b6965d372c.a) on / type ffs (rw, local, ctime=Sun Jan  3 18:03:00 2021)
             /dev/wd0e (d1c342b6965d372c.e) on /home type ffs (rw, local, nodevl, nosuid, ctime=Sun Jan  3 18:02:56 2021)
             /dev/wd0d (d1c342b6965d372c.d) on /usr type ffs (rw, local, nodev, wxallowed, ctime=Sun Jan  3 18:02:56 2021)
             */
            String[] split = ParseUtil.whitespaces.split(fs, 7);
            if (split.length == 7) {
                // 1st field is volume name [0-index] + partition letter
                // 2nd field is disklabel UUID (DUID) + partition letter after the dot
                // 4th field is mount point
                // 6rd field is fs type
                // 7th field is options
                String volume = split[0];
                String uuid = split[1];
                String path = split[3];
                String type = split[5];
                String options = split[6];

                // Skip non-local drives if requested, and exclude pseudo file systems
                if ((localOnly && NETWORK_FS_TYPES.contains(type)) || !path.equals("/") && (
                    PSEUDO_FS_TYPES.contains(type) || FileSystemUtil.isFileStoreExcluded(path,
                        volume, FS_PATH_INCLUDES, FS_PATH_EXCLUDES, FS_VOLUME_INCLUDES,
                        FS_VOLUME_EXCLUDES))) {
                    continue;
                }

                String name = path.substring(path.lastIndexOf('/') + 1);
                // Special case for /, pull last element of volume instead
                if (name.isEmpty()) {
                    name = volume.substring(volume.lastIndexOf('/') + 1);
                }

                if (nameToMatch != null && !nameToMatch.equals(name)) {
                    continue;
                }
                File f = new File(path);
                long totalSpace = f.getTotalSpace();
                long usableSpace = f.getUsableSpace();
                long freeSpace = f.getFreeSpace();

                String description;
                if (volume.startsWith("/dev") || path.equals("/")) {
                    description = "Local Disk";
                } else if (volume.equals("tmpfs")) {
                    // dynamic size in memory FS
                    description = "Ram Disk (dynamic)";
                } else if (volume.equals("mfs")) {
                    // fixed size in memory FS
                    description = "Ram Disk (fixed)";
                } else if (NETWORK_FS_TYPES.contains(type)) {
                    description = "Network Disk";
                } else {
                    description = "Mount Point";
                }

                fsList.add(
                    new OpenBsdOSFileStore(name, volume, name, path, options, uuid, "", description,
                        type, freeSpace, usableSpace, totalSpace,
                        inodeFreeMap.getOrDefault(volume, 0L),
                        inodeUsedlMap.getOrDefault(volume, 0L) + inodeFreeMap.getOrDefault(volume,
                            0L)));
            }
        }
        return fsList;
    }

    @Override public List<OSFileStore> getFileStores(boolean localOnly) {
        return getFileStoreMatching(null, localOnly);
    }

    @Override public long getOpenFileDescriptors() {
        return OpenBsdSysctlUtil.sysctl("kern.nfiles", 0);
    }

    @Override public long getMaxFileDescriptors() {
        return OpenBsdSysctlUtil.sysctl("kern.maxfiles", 0);
    }
}
