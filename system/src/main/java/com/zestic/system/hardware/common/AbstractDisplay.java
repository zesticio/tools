
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Display;
import com.zestic.system.util.EdidUtil;

import java.util.Arrays;

/*
 * A Display
 */
@Immutable public abstract class AbstractDisplay implements Display {

    private final byte[] edid;

    /*
     * Constructor for AbstractDisplay.
     *
     * @param edid a byte array representing a display EDID
     */
    protected AbstractDisplay(byte[] edid) {
        this.edid = Arrays.copyOf(edid, edid.length);
    }

    @Override public byte[] getEdid() {
        return Arrays.copyOf(this.edid, this.edid.length);
    }

    @Override public String toString() {
        return EdidUtil.toString(this.edid);
    }
}
