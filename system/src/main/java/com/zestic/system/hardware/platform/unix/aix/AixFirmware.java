
package com.zestic.system.hardware.platform.unix.aix;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.common.AbstractFirmware;

/*
 * Firmware data.
 */
@Immutable final class AixFirmware extends AbstractFirmware {

    private final String manufacturer;
    private final String name;
    private final String version;

    AixFirmware(String manufacturer, String name, String version) {
        this.manufacturer = manufacturer;
        this.name = name;
        this.version = version;
    }

    @Override public String getManufacturer() {
        return manufacturer;
    }

    @Override public String getName() {
        return name;
    }

    @Override public String getVersion() {
        return version;
    }
}
