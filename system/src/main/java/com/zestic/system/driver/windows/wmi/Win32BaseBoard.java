
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Objects;

/*
 * Utility to query WMI class {@code Win32_BaseBoard}
 */
@ThreadSafe public final class Win32BaseBoard {

    private static final String WIN32_BASEBOARD = "Win32_BaseBoard";


    private Win32BaseBoard() {
    }

    /*
     * Queries the Baseboard description.
     *
     * @return Baseboard manufacturer, model, and related fields.
     */
    public static WmiResult<BaseBoardProperty> queryBaseboardInfo() {
        WmiQuery<BaseBoardProperty> baseboardQuery =
            new WmiQuery<>(WIN32_BASEBOARD, BaseBoardProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(baseboardQuery);
    }

    /*
     * Baseboard description properties.
     */
    public enum BaseBoardProperty {
        MANUFACTURER, MODEL, VERSION, SERIALNUMBER;
    }
}
