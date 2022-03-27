
package com.zestic.system.hardware.platform.unix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.Xrandr;
import com.zestic.system.hardware.Display;
import com.zestic.system.hardware.common.AbstractDisplay;

import java.util.List;
import java.util.stream.Collectors;

/*
 * A Display
 */
@ThreadSafe public final class UnixDisplay extends AbstractDisplay {

    /*
     * Constructor for UnixDisplay.
     *
     * @param edid a byte array representing a display EDID
     */
    UnixDisplay(byte[] edid) {
        super(edid);
    }

    /*
     * Gets Display Information
     *
     * @return An array of Display objects representing monitors, etc.
     */
    public static List<Display> getDisplays() {
        return Xrandr.getEdidArrays().stream().map(UnixDisplay::new).collect(Collectors.toList());
    }
}
