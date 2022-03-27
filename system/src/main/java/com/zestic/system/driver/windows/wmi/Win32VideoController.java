
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Objects;

/*
 * Utility to query WMI class {@code Win32_VideoController}
 */
@ThreadSafe public final class Win32VideoController {

    private static final String WIN32_VIDEO_CONTROLLER = "Win32_VideoController";


    private Win32VideoController() {
    }

    /*
     * Queries video controller info for Vista and later.
     *
     * @return Information regarding video controllers
     */
    public static WmiResult<VideoControllerProperty> queryVideoController() {
        WmiQuery<VideoControllerProperty> videoControllerQuery =
            new WmiQuery<>(WIN32_VIDEO_CONTROLLER, VideoControllerProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance())
            .queryWMI(videoControllerQuery);
    }

    /*
     * Video Controller properties
     */
    public enum VideoControllerProperty {
        ADAPTERCOMPATIBILITY, ADAPTERRAM, DRIVERVERSION, NAME, PNPDEVICEID;
    }
}
