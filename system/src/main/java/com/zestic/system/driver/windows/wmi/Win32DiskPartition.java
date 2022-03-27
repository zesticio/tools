
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

/*
 * Utility to query WMI class {@code Win32_DiskPartition}
 */
@ThreadSafe public final class Win32DiskPartition {

    private static final String WIN32_DISK_PARTITION = "Win32_DiskPartition";


    private Win32DiskPartition() {
    }

    /*
     * Queries the partition.
     *
     * @param h An instantiated {@link WmiQueryHandler}. User should have already
     *          initialized COM.
     * @return Information regarding each disk partition.
     */
    public static WmiResult<DiskPartitionProperty> queryPartition(WmiQueryHandler h) {
        WmiQuery<DiskPartitionProperty> partitionQuery =
            new WmiQuery<>(WIN32_DISK_PARTITION, DiskPartitionProperty.class);
        return h.queryWMI(partitionQuery, false);
    }

    /*
     * Disk partition properties
     */
    public enum DiskPartitionProperty {
        INDEX, DESCRIPTION, DEVICEID, DISKINDEX, NAME, SIZE, TYPE;
    }
}
