
package com.zestic.system.jna.platform.unix.aix;

import com.sun.jna.Native;
import com.zestic.system.jna.platform.unix.CLibrary;

/*
 * C library. This class should be considered non-API as it may be removed
 * if/when its code is incorporated into the JNA project.
 */
public interface AixLibc extends CLibrary {

    AixLibc INSTANCE = Native.load("c", AixLibc.class);

}
