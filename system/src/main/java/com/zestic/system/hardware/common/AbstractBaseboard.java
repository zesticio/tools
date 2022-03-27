
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Baseboard;

/*
 * Baseboard data
 */
@Immutable public abstract class AbstractBaseboard implements Baseboard {

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("manufacturer=").append(getManufacturer()).append(", ");
        sb.append("model=").append(getModel()).append(", ");
        sb.append("version=").append(getVersion()).append(", ");
        sb.append("serial number=").append(getSerialNumber());
        return sb.toString();
    }

}
