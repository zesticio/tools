
package com.zestic.system.hardware;

import com.zestic.system.annotation.concurrent.Immutable;

/*
 * Display refers to the information regarding a video source and monitor
 * identified by the EDID standard.
 */
@Immutable public interface Display {
    /*
     * The EDID byte array.
     *
     * @return The original unparsed EDID byte array.
     */
    byte[] getEdid();
}
