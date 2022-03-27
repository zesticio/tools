
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Firmware;
import com.zestic.system.util.Constants;

/*
 * Firmware data.
 */
@Immutable public abstract class AbstractFirmware implements Firmware {

    /*
     * Multiple classes don't have these, set defaults here
     */

    @Override public String getName() {
        return Constants.UNKNOWN;
    }

    @Override public String getDescription() {
        return Constants.UNKNOWN;
    }

    @Override public String getReleaseDate() {
        return Constants.UNKNOWN;
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("manufacturer=").append(getManufacturer()).append(", ");
        sb.append("name=").append(getName()).append(", ");
        sb.append("description=").append(getDescription()).append(", ");
        sb.append("version=").append(getVersion()).append(", ");
        sb.append("release date=").append(getReleaseDate() == null ? "unknown" : getReleaseDate());
        return sb.toString();
    }

}
