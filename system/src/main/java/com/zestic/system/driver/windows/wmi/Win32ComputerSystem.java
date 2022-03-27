
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Objects;

/*
 * Utility to query WMI class {@code Win32_ComputerSystem}
 */
@ThreadSafe public final class Win32ComputerSystem {

    private static final String WIN32_COMPUTER_SYSTEM = "Win32_ComputerSystem";


    private Win32ComputerSystem() {
    }

    /*
     * Queries the Computer System.
     *
     * @return Computer System Manufacturer and Model
     */
    public static WmiResult<ComputerSystemProperty> queryComputerSystem() {
        WmiQuery<ComputerSystemProperty> computerSystemQuery =
            new WmiQuery<>(WIN32_COMPUTER_SYSTEM, ComputerSystemProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance())
            .queryWMI(computerSystemQuery);
    }

    /*
     * Computer System properties
     */
    public enum ComputerSystemProperty {
        MANUFACTURER, MODEL;
    }
}
