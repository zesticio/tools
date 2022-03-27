
package com.zestic.system.hardware.platform.unix;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.common.AbstractBaseboard;

/*
 * Baseboard data obtained by a calling class
 */
@Immutable public final class UnixBaseboard extends AbstractBaseboard {

    private final String manufacturer;
    private final String model;
    private final String serialNumber;
    private final String version;

    public UnixBaseboard(String manufacturer, String model, String serialNumber, String version) {
        this.manufacturer = manufacturer;
        this.model = model;
        this.serialNumber = serialNumber;
        this.version = version;
    }

    @Override public String getManufacturer() {
        return this.manufacturer;
    }

    @Override public String getModel() {
        return this.model;
    }

    @Override public String getSerialNumber() {
        return this.serialNumber;
    }

    @Override public String getVersion() {
        return this.version;
    }
}
