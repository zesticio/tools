
package com.zestic.system.hardware.platform.linux;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.driver.linux.*;
import com.zestic.system.driver.linux.proc.CpuInfo;
import com.zestic.system.hardware.Baseboard;
import com.zestic.system.hardware.Firmware;
import com.zestic.system.hardware.common.AbstractComputerSystem;
import com.zestic.system.util.Constants;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * Hardware data obtained from sysfs.
 */
@Immutable final class LinuxComputerSystem extends AbstractComputerSystem {

    private final Supplier<String> manufacturer = memoize(LinuxComputerSystem::queryManufacturer);

    private final Supplier<String> model = memoize(LinuxComputerSystem::queryModel);

    private final Supplier<String> serialNumber = memoize(LinuxComputerSystem::querySerialNumber);

    private final Supplier<String> uuid = memoize(LinuxComputerSystem::queryUUID);

    private static String queryManufacturer() {
        String result = null;
        if ((result = Sysfs.querySystemVendor()) == null
            && (result = CpuInfo.queryCpuManufacturer()) == null) {
            return Constants.UNKNOWN;
        }
        return result;
    }

    private static String queryModel() {
        String result = null;
        if ((result = Sysfs.queryProductModel()) == null
            && (result = Devicetree.queryModel()) == null && (result = Lshw.queryModel()) == null) {
            return Constants.UNKNOWN;
        }
        return result;
    }

    private static String querySerialNumber() {
        String result = null;
        if ((result = Sysfs.queryProductSerial()) == null
            && (result = Dmidecode.querySerialNumber()) == null
            && (result = Lshal.querySerialNumber()) == null
            && (result = Lshw.querySerialNumber()) == null) {
            return Constants.UNKNOWN;
        }
        return result;
    }

    private static String queryUUID() {
        String result = null;
        if ((result = Sysfs.queryUUID()) == null && (result = Dmidecode.queryUUID()) == null
            && (result = Lshal.queryUUID()) == null && (result = Lshw.queryUUID()) == null) {
            return Constants.UNKNOWN;
        }
        return result;
    }

    @Override public String getManufacturer() {
        return manufacturer.get();
    }

    @Override public String getModel() {
        return model.get();
    }

    @Override public String getSerialNumber() {
        return serialNumber.get();
    }

    @Override public String getHardwareUUID() {
        return uuid.get();
    }

    @Override public Firmware createFirmware() {
        return new LinuxFirmware();
    }

    @Override public Baseboard createBaseboard() {
        return new LinuxBaseboard();
    }
}
