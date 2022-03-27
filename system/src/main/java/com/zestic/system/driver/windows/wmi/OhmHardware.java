
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;
import com.zestic.system.util.platform.windows.WmiUtil;

/*
 * Utility to query Open Hardware Monitor WMI data for Hardware
 */
@ThreadSafe public final class OhmHardware {

    private static final String HARDWARE = "Hardware";


    private OhmHardware() {
    }

    /*
     * Queries the hardware identifiers for a monitored type.
     *
     * @param h           An instantiated {@link WmiQueryHandler}. User should have already
     *                    initialized COM.
     * @param typeToQuery which type to filter based on
     * @param typeName    the name of the type
     * @return The sensor value.
     */
    public static WmiResult<IdentifierProperty> queryHwIdentifier(WmiQueryHandler h,
        String typeToQuery, String typeName) {
        StringBuilder sb = new StringBuilder(HARDWARE);
        sb.append(" WHERE ").append(typeToQuery).append("Type=\"").append(typeName).append('\"');
        WmiQuery<IdentifierProperty> cpuIdentifierQuery =
            new WmiQuery<>(WmiUtil.OHM_NAMESPACE, sb.toString(), IdentifierProperty.class);
        return h.queryWMI(cpuIdentifierQuery, false);
    }

    /*
     * HW Identifier Property
     */
    public enum IdentifierProperty {
        IDENTIFIER;
    }
}
