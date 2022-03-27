
package com.zestic.system.software.os;

import com.zestic.system.annotation.concurrent.ThreadSafe;

/*
 * A FileStore represents a storage pool, device, partition, volume, concrete
 * file system or other implementation specific means of file storage. This
 * object carries the same interpretation as core Java's
 * {@link java.nio.file.FileStore} class, with additional information.
 */
@ThreadSafe public interface OSFileStore {

    /*
     * Name of the File System
     *
     * @return The file system name
     */
    String getName();

    /*
     * Volume name of the File System
     *
     * @return The volume name of the file system
     */
    String getVolume();

    /*
     * Label of the File System
     *
     * @return The volume label of the file system. Only relevant on Windows and on
     * Linux, if assigned; otherwise defaults to the FileSystem name. On
     * other operating systems is redundant with the name.
     */
    String getLabel();

    /*
     * Logical volume of the File System
     * <p>
     * Provides an optional alternative volume identifier for the file system. Only
     * supported on Linux, provides symlink value via '/dev/mapper/' (used with LVM
     * file systems).
     *
     * @return The logical volume of the file system
     */
    String getLogicalVolume();

    /*
     * Mountpoint of the File System
     *
     * @return The mountpoint of the file system
     */
    String getMount();

    /*
     * Description of the File System
     *
     * @return The file system description
     */
    String getDescription();

    /*
     * Type of the File System (FAT, NTFS, etx2, ext4, etc)
     *
     * @return The file system type
     */
    String getType();

    /*
     * Filesystem options
     *
     * @return A comma-deimited string of options
     */
    String getOptions();

    /*
     * UUID/GUID of the File System
     *
     * @return The file system UUID/GUID
     */
    String getUUID();

    /*
     * Free space on the drive. This space is unallocated but may require elevated
     * permissions to write.
     *
     * @return Free space on the drive (in bytes)
     */
    long getFreeSpace();

    /*
     * Usable space on the drive. This is space available to unprivileged users.
     *
     * @return Usable space on the drive (in bytes)
     */
    long getUsableSpace();

    /*
     * Total space/capacity of the drive.
     *
     * @return Total capacity of the drive (in bytes)
     */
    long getTotalSpace();

    /*
     * Usable / free inodes on the drive. Not applicable on Windows.
     *
     * @return Usable / free inodes on the drive (count), or -1 if unimplemented
     */
    long getFreeInodes();

    /*
     * Total / maximum number of inodes of the filesystem. Not applicable on
     * Windows.
     *
     * @return Total / maximum number of inodes of the filesystem (count), or -1 if
     * unimplemented
     */
    long getTotalInodes();

    /*
     * Make a best effort to update all the statistics about the file store without
     * needing to recreate the file store list. This method provides for more
     * frequent periodic updates of file store statistics.
     *
     * @return True if the update was (probably) successful, false if the disk was
     * not found
     */
    boolean updateAttributes();
}
