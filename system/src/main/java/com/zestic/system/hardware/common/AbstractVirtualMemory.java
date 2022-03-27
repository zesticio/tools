
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.VirtualMemory;
import com.zestic.system.util.FormatUtil;

/*
 * Virtual Memory info.
 */
@ThreadSafe public abstract class AbstractVirtualMemory implements VirtualMemory {

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Swap Used/Avail: ");
        sb.append(FormatUtil.formatBytes(getSwapUsed()));
        sb.append("/");
        sb.append(FormatUtil.formatBytes(getSwapTotal()));
        sb.append(", Virtual Memory In Use/Max=");
        sb.append(FormatUtil.formatBytes(getVirtualInUse()));
        sb.append("/");
        sb.append(FormatUtil.formatBytes(getVirtualMax()));
        return sb.toString();
    }
}
