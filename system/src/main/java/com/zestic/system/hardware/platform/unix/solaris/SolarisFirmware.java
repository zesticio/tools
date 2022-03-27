
package com.zestic.system.hardware.platform.unix.solaris;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.common.AbstractFirmware;

/*
 * Firmware data.
 */
@Immutable final class SolarisFirmware extends AbstractFirmware {

    private final String manufacturer;
    private final String version;
    private final String releaseDate;

    SolarisFirmware(String manufacturer, String version, String releaseDate) {
        this.manufacturer = manufacturer;
        this.version = version;
        this.releaseDate = releaseDate;
    }

    @Override public String getManufacturer() {
        return manufacturer;
    }

    @Override public String getVersion() {
        return version;
    }

    @Override public String getReleaseDate() {
        return releaseDate;
    }
}
