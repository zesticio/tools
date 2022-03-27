
package com.zestic.system.hardware.platform.linux;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.PowerSource;
import com.zestic.system.hardware.common.AbstractPowerSource;
import com.zestic.system.util.Constants;
import com.zestic.system.util.FileUtil;
import com.zestic.system.util.ParseUtil;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * A Power Source
 */
@ThreadSafe public final class LinuxPowerSource extends AbstractPowerSource {

    private static final String PS_PATH = "/sys/class/power_supply/";

    public LinuxPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent,
        double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate,
        double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging,
        boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity,
        int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry,
        LocalDate psManufactureDate, String psManufacturer, String psSerialNumber,
        double psTemperature) {
        super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated,
            psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine,
            psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity,
            psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer,
            psSerialNumber, psTemperature);
    }

    /*
     * Gets Battery Information
     *
     * @return An array of PowerSource objects representing batteries, etc.
     */
    public static List<PowerSource> getPowerSources() {
        String psName;
        String psDeviceName;
        double psRemainingCapacityPercent = -1d;
        double psTimeRemainingEstimated = -1d; // -1 = unknown, -2 = unlimited
        double psTimeRemainingInstant = -1d;
        double psPowerUsageRate = 0d;
        double psVoltage = -1d;
        double psAmperage = 0d;
        boolean psPowerOnLine = false;
        boolean psCharging = false;
        boolean psDischarging = false;
        PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
        int psCurrentCapacity = -1;
        int psMaxCapacity = -1;
        int psDesignCapacity = -1;
        int psCycleCount = -1;
        String psChemistry;
        LocalDate psManufactureDate = null;
        String psManufacturer;
        String psSerialNumber;
        double psTemperature = 0d;

        // Get list of power source names
        File f = new File(PS_PATH);
        String[] psNames = f.list();
        List<PowerSource> psList = new ArrayList<>();
        // Empty directory will give null rather than empty array, so fix
        if (psNames != null) {
            // For each power source, output various info
            for (String name : psNames) {
                // Skip if name is ADP* or AC* (AC power supply)
                if (!name.startsWith("ADP") && !name.startsWith("AC")) {
                    // Skip if can't read uevent file
                    List<String> psInfo;
                    psInfo = FileUtil.readFile(PS_PATH + name + "/uevent", false);
                    if (psInfo.isEmpty()) {
                        continue;
                    }
                    Map<String, String> psMap = new HashMap<>();
                    for (String line : psInfo) {
                        String[] split = line.split("=");
                        if (split.length > 1 && !split[1].isEmpty()) {
                            psMap.put(split[0], split[1]);
                        }
                    }
                    psName = psMap.getOrDefault("POWER_SUPPLY_NAME", name);
                    String status = psMap.get("POWER_SUPPLY_STATUS");
                    psCharging = "Charging".equals(status);
                    psDischarging = "Discharging".equals(status);
                    if (psMap.containsKey("POWER_SUPPLY_CAPACITY")) {
                        psRemainingCapacityPercent =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CAPACITY"), -100)
                                / 100d;
                    }
                    if (psMap.containsKey("POWER_SUPPLY_ENERGY_NOW")) {
                        psCurrentCapacity =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_ENERGY_NOW"), -1);
                    } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_NOW")) {
                        psCurrentCapacity =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CHARGE_NOW"), -1);
                    }
                    if (psMap.containsKey("POWER_SUPPLY_ENERGY_FULL")) {
                        psCurrentCapacity =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_ENERGY_FULL"), 1);
                    } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_FULL")) {
                        psCurrentCapacity =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CHARGE_FULL"), 1);
                    }
                    if (psMap.containsKey("POWER_SUPPLY_ENERGY_FULL_DESIGN")) {
                        psMaxCapacity = ParseUtil.parseIntOrDefault(
                            psMap.get("POWER_SUPPLY_ENERGY_FULL_DESIGN"), 1);
                    } else if (psMap.containsKey("POWER_SUPPLY_CHARGE_FULL_DESIGN")) {
                        psMaxCapacity = ParseUtil.parseIntOrDefault(
                            psMap.get("POWER_SUPPLY_CHARGE_FULL_DESIGN"), 1);
                    }
                    if (psMap.containsKey("POWER_SUPPLY_VOLTAGE_NOW")) {
                        psVoltage =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_VOLTAGE_NOW"), -1);
                    }
                    if (psMap.containsKey("POWER_SUPPLY_POWER_NOW")) {
                        psPowerUsageRate =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_POWER_NOW"), -1);
                    }
                    if (psVoltage > 0) {
                        psAmperage = psPowerUsageRate / psVoltage;
                    }
                    if (psMap.containsKey("POWER_SUPPLY_CYCLE_COUNT")) {
                        psCycleCount =
                            ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_CYCLE_COUNT"), -1);
                    }
                    psChemistry = psMap.getOrDefault("POWER_SUPPLY_TECHNOLOGY", Constants.UNKNOWN);
                    psDeviceName = psMap.getOrDefault("POWER_SUPPLY_MODEL_NAME", Constants.UNKNOWN);
                    psManufacturer =
                        psMap.getOrDefault("POWER_SUPPLY_MANUFACTURER", Constants.UNKNOWN);
                    psSerialNumber =
                        psMap.getOrDefault("POWER_SUPPLY_SERIAL_NUMBER", Constants.UNKNOWN);
                    if (ParseUtil.parseIntOrDefault(psMap.get("POWER_SUPPLY_PRESENT"), 1) > 0) {
                        psList.add(
                            new LinuxPowerSource(psName, psDeviceName, psRemainingCapacityPercent,
                                psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate,
                                psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging,
                                psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity,
                                psCycleCount, psChemistry, psManufactureDate, psManufacturer,
                                psSerialNumber, psTemperature));
                    }
                }
            }
        }
        return psList;
    }
}
