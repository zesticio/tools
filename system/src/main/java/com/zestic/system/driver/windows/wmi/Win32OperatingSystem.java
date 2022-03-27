
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Objects;

/*
 * Utility to query WMI class {@code Win32_OperatingSystem}
 */
@ThreadSafe public final class Win32OperatingSystem {

    private static final String WIN32_OPERATING_SYSTEM = "Win32_OperatingSystem";


    private Win32OperatingSystem() {
    }

    /*
     * Queries the Computer System.
     *
     * @return Computer System Manufacturer and Model
     */
    public static WmiResult<OSVersionProperty> queryOsVersion() {
        WmiQuery<OSVersionProperty> osVersionQuery =
            new WmiQuery<>(WIN32_OPERATING_SYSTEM, OSVersionProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(osVersionQuery);
    }

    /*
     * Operating System properties
     */
    public enum OSVersionProperty {
        VERSION, PRODUCTTYPE, BUILDNUMBER, CSDVERSION, SUITEMASK;
    }
}
