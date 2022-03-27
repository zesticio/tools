
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.driver.windows.wmi.Win32Bios;
import com.zestic.system.driver.windows.wmi.Win32ComputerSystem;
import com.zestic.system.driver.windows.wmi.Win32ComputerSystem.ComputerSystemProperty;
import com.zestic.system.driver.windows.wmi.Win32ComputerSystemProduct;
import com.zestic.system.hardware.Baseboard;
import com.zestic.system.hardware.Firmware;
import com.zestic.system.hardware.common.AbstractComputerSystem;
import com.zestic.system.util.Constants;
import com.zestic.system.util.Util;
import com.zestic.system.util.platform.windows.WmiUtil;
import com.zestic.system.util.tuples.Pair;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * Hardware data obtained from WMI.
 */
@Immutable final class WindowsComputerSystem extends AbstractComputerSystem {

    private final Supplier<Pair<String, String>> manufacturerModel =
        memoize(WindowsComputerSystem::queryManufacturerModel);
    private final Supplier<Pair<String, String>> serialNumberUUID =
        memoize(WindowsComputerSystem::querySystemSerialNumberUUID);

    private static Pair<String, String> queryManufacturerModel() {
        String manufacturer = null;
        String model = null;
        WmiResult<ComputerSystemProperty> win32ComputerSystem =
            Win32ComputerSystem.queryComputerSystem();
        if (win32ComputerSystem.getResultCount() > 0) {
            manufacturer =
                WmiUtil.getString(win32ComputerSystem, ComputerSystemProperty.MANUFACTURER, 0);
            model = WmiUtil.getString(win32ComputerSystem, ComputerSystemProperty.MODEL, 0);
        }
        return new Pair<>(Util.isBlank(manufacturer) ? Constants.UNKNOWN : manufacturer,
            Util.isBlank(model) ? Constants.UNKNOWN : model);
    }

    private static Pair<String, String> querySystemSerialNumberUUID() {
        String serialNumber = null;
        String uuid = null;
        WmiResult<Win32ComputerSystemProduct.ComputerSystemProductProperty>
            win32ComputerSystemProduct = Win32ComputerSystemProduct.queryIdentifyingNumberUUID();
        if (win32ComputerSystemProduct.getResultCount() > 0) {
            serialNumber = WmiUtil.getString(win32ComputerSystemProduct,
                Win32ComputerSystemProduct.ComputerSystemProductProperty.IDENTIFYINGNUMBER, 0);
            uuid = WmiUtil.getString(win32ComputerSystemProduct,
                Win32ComputerSystemProduct.ComputerSystemProductProperty.UUID, 0);
        }
        if (Util.isBlank(serialNumber)) {
            serialNumber = querySerialFromBios();
        }
        if (Util.isBlank(serialNumber)) {
            serialNumber = Constants.UNKNOWN;
        }
        if (Util.isBlank(uuid)) {
            uuid = Constants.UNKNOWN;
        }
        return new Pair<>(serialNumber, uuid);
    }

    private static String querySerialFromBios() {
        WmiResult<Win32Bios.BiosSerialProperty> serialNum = Win32Bios.querySerialNumber();
        if (serialNum.getResultCount() > 0) {
            return WmiUtil.getString(serialNum, Win32Bios.BiosSerialProperty.SERIALNUMBER, 0);
        }
        return null;
    }

    @Override public String getManufacturer() {
        return manufacturerModel.get().getA();
    }

    @Override public String getModel() {
        return manufacturerModel.get().getB();
    }

    @Override public String getSerialNumber() {
        return serialNumberUUID.get().getA();
    }

    @Override public String getHardwareUUID() {
        return serialNumberUUID.get().getB();
    }

    @Override public Firmware createFirmware() {
        return new WindowsFirmware();
    }

    @Override public Baseboard createBaseboard() {
        return new WindowsBaseboard();
    }
}
