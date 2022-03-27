
package com.zestic.system.hardware;

import com.zestic.system.annotation.concurrent.Immutable;

/*
 * The Baseboard represents the system board, also called motherboard, logic
 * board, etc.
 */
@Immutable public interface Baseboard {
    /*
     * Get the baseboard manufacturer.
     *
     * @return The manufacturer.
     */
    String getManufacturer();

    /*
     * Get the baseboard model.
     *
     * @return The model.
     */
    String getModel();

    /*
     * Get the baseboard version.
     *
     * @return The version.
     */
    String getVersion();

    /*
     * Get the baseboard serial number.
     *
     * @return The serial number.
     */
    String getSerialNumber();
}
