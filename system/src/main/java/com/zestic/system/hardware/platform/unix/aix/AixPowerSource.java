
package com.zestic.system.hardware.platform.unix.aix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.PowerSource;
import com.zestic.system.hardware.common.AbstractPowerSource;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/*
 * A Power Source
 */
@ThreadSafe public final class AixPowerSource extends AbstractPowerSource {

    public AixPowerSource(String name, String deviceName, double remainingCapacityPercent,
        double timeRemainingEstimated, double timeRemainingInstant, double powerUsageRate,
        double voltage, double amperage, boolean powerOnLine, boolean charging, boolean discharging,
        CapacityUnits capacityUnits, int currentCapacity, int maxCapacity, int designCapacity,
        int cycleCount, String chemistry, LocalDate manufactureDate, String manufacturer,
        String serialNumber, double temperature) {
        super(name, deviceName, remainingCapacityPercent, timeRemainingEstimated,
            timeRemainingInstant, powerUsageRate, voltage, amperage, powerOnLine, charging,
            discharging, capacityUnits, currentCapacity, maxCapacity, designCapacity, cycleCount,
            chemistry, manufactureDate, manufacturer, serialNumber, temperature);
    }

    /*
     * Gets Battery Information. AIX does not provide any battery statistics, as
     * most servers are not designed to be run on battery.
     *
     * @return An empty list.
     */
    public static List<PowerSource> getPowerSources() {
        return Collections.emptyList();
    }
}
