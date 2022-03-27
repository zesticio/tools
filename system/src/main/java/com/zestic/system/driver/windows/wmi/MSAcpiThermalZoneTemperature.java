
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Objects;

/*
 * Utility to query WMI class {@code MSAcpi_ThermalZoneTemperature}
 */
@ThreadSafe public final class MSAcpiThermalZoneTemperature {

    public static final String WMI_NAMESPACE = "ROOT\\WMI";
    private static final String MS_ACPI_THERMAL_ZONE_TEMPERATURE = "MSAcpi_ThermalZoneTemperature";


    private MSAcpiThermalZoneTemperature() {
    }

    /*
     * Queries the current temperature
     *
     * @return Temperature at thermal zone in tenths of degrees Kelvin.
     */
    public static WmiResult<TemperatureProperty> queryCurrentTemperature() {
        WmiQuery<TemperatureProperty> curTempQuery =
            new WmiQuery<>(WMI_NAMESPACE, MS_ACPI_THERMAL_ZONE_TEMPERATURE,
                TemperatureProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(curTempQuery);
    }

    /*
     * Current temperature property.
     */
    public enum TemperatureProperty {
        CURRENTTEMPERATURE;
    }
}
