
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Baseboard;
import com.zestic.system.hardware.ComputerSystem;
import com.zestic.system.hardware.Firmware;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * Computer System data.
 */
@Immutable public abstract class AbstractComputerSystem implements ComputerSystem {

    private final Supplier<Firmware> firmware = memoize(this::createFirmware);

    private final Supplier<Baseboard> baseboard = memoize(this::createBaseboard);

    @Override public Firmware getFirmware() {
        return firmware.get();
    }

    /*
     * Instantiates the platform-specific {@link Firmware} object
     *
     * @return platform-specific {@link Firmware} object
     */
    protected abstract Firmware createFirmware();

    @Override public Baseboard getBaseboard() {
        return baseboard.get();
    }

    /*
     * Instantiates the platform-specific {@link Baseboard} object
     *
     * @return platform-specific {@link Baseboard} object
     */
    protected abstract Baseboard createBaseboard();

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("manufacturer=").append(getManufacturer()).append(", ");
        sb.append("model=").append(getModel()).append(", ");
        sb.append("serial number=").append(getSerialNumber()).append(", ");
        sb.append("uuid=").append(getHardwareUUID());
        return sb.toString();
    }
}
