
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Objects;

/*
 * Utility to query WMI class {@code Win32_ComputerSystemProduct}
 */
@ThreadSafe public final class Win32ComputerSystemProduct {

    private static final String WIN32_COMPUTER_SYSTEM_PRODUCT = "Win32_ComputerSystemProduct";


    private Win32ComputerSystemProduct() {
    }

    /*
     * Queries the Computer System Product.
     *
     * @return Assigned serial number and UUID.
     */
    public static WmiResult<ComputerSystemProductProperty> queryIdentifyingNumberUUID() {
        WmiQuery<ComputerSystemProductProperty> identifyingNumberQuery =
            new WmiQuery<>(WIN32_COMPUTER_SYSTEM_PRODUCT, ComputerSystemProductProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance())
            .queryWMI(identifyingNumberQuery);
    }

    /*
     * Computer System ID number
     */
    public enum ComputerSystemProductProperty {
        IDENTIFYINGNUMBER, UUID;
    }
}
