
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

/*
 * Utility to query WMI class {@code Win32_LogicalDiskToPartition}
 */
@ThreadSafe public final class Win32LogicalDiskToPartition {

    private static final String WIN32_LOGICAL_DISK_TO_PARTITION = "Win32_LogicalDiskToPartition";


    private Win32LogicalDiskToPartition() {
    }

    /*
     * Queries the association between logical disk and partition.
     *
     * @param h An instantiated {@link WmiQueryHandler}. User should have already
     *          initialized COM.
     * @return Antecedent-dependent pairs of disk and partition.
     */
    public static WmiResult<DiskToPartitionProperty> queryDiskToPartition(WmiQueryHandler h) {
        WmiQuery<DiskToPartitionProperty> diskToPartitionQuery =
            new WmiQuery<>(WIN32_LOGICAL_DISK_TO_PARTITION, DiskToPartitionProperty.class);
        return h.queryWMI(diskToPartitionQuery, false);
    }

    /*
     * Links disk drives to partitions
     */
    public enum DiskToPartitionProperty {
        ANTECEDENT, DEPENDENT, ENDINGADDRESS, STARTINGADDRESS;
    }
}
