
package com.zestic.system.jna.platform.windows;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

public interface WinNT extends com.sun.jna.platform.win32.WinNT {

    @FieldOrder({"TokenIsElevated"}) class TOKEN_ELEVATION extends Structure {
        public int TokenIsElevated;
    }
}
