
package com.zestic.system.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKitUtil;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Baseboard;
import com.zestic.system.hardware.Firmware;
import com.zestic.system.hardware.common.AbstractComputerSystem;
import com.zestic.system.util.Constants;
import com.zestic.system.util.Util;
import com.zestic.system.util.tuples.Quartet;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * Hardware data obtained from ioreg.
 */
@Immutable final class MacComputerSystem extends AbstractComputerSystem {

    private final Supplier<Quartet<String, String, String, String>> manufacturerModelSerialUUID =
        memoize(MacComputerSystem::platformExpert);

    private static Quartet<String, String, String, String> platformExpert() {
        String manufacturer = null;
        String model = null;
        String serialNumber = null;
        String uuid = null;
        IORegistryEntry platformExpert = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
        if (platformExpert != null) {
            byte[] data = platformExpert.getByteArrayProperty("manufacturer");
            if (data != null) {
                manufacturer = Native.toString(data, StandardCharsets.UTF_8);
            }
            data = platformExpert.getByteArrayProperty("model");
            if (data != null) {
                model = Native.toString(data, StandardCharsets.UTF_8);
            }
            serialNumber = platformExpert.getStringProperty("IOPlatformSerialNumber");
            uuid = platformExpert.getStringProperty("IOPlatformUUID");
            platformExpert.release();
        }
        return new Quartet<>(Util.isBlank(manufacturer) ? "Apple Inc." : manufacturer,
            Util.isBlank(model) ? Constants.UNKNOWN : model,
            Util.isBlank(serialNumber) ? Constants.UNKNOWN : serialNumber,
            Util.isBlank(uuid) ? Constants.UNKNOWN : uuid);
    }

    @Override public String getManufacturer() {
        return manufacturerModelSerialUUID.get().getA();
    }

    @Override public String getModel() {
        return manufacturerModelSerialUUID.get().getB();
    }

    @Override public String getSerialNumber() {
        return manufacturerModelSerialUUID.get().getC();
    }

    @Override public String getHardwareUUID() {
        return manufacturerModelSerialUUID.get().getD();
    }

    @Override public Firmware createFirmware() {
        return new MacFirmware();
    }

    @Override public Baseboard createBaseboard() {
        return new MacBaseboard();
    }
}
