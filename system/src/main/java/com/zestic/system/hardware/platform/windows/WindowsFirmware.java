
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.driver.windows.wmi.Win32Bios;
import com.zestic.system.hardware.common.AbstractFirmware;
import com.zestic.system.util.Constants;
import com.zestic.system.util.Memoizer;
import com.zestic.system.util.Util;
import com.zestic.system.util.platform.windows.WmiUtil;
import com.zestic.system.util.tuples.Quintet;

import java.util.function.Supplier;

/*
 * Firmware data obtained from WMI
 */
@Immutable final class WindowsFirmware extends AbstractFirmware {

    private final Supplier<Quintet<String, String, String, String, String>>
        manufNameDescVersRelease = Memoizer.memoize(WindowsFirmware::queryManufNameDescVersRelease);

    private static Quintet<String, String, String, String, String> queryManufNameDescVersRelease() {
        String manufacturer = null;
        String name = null;
        String description = null;
        String version = null;
        String releaseDate = null;
        WmiResult<Win32Bios.BiosProperty> win32BIOS = Win32Bios.queryBiosInfo();
        if (win32BIOS.getResultCount() > 0) {
            manufacturer = WmiUtil.getString(win32BIOS, Win32Bios.BiosProperty.MANUFACTURER, 0);
            name = WmiUtil.getString(win32BIOS, Win32Bios.BiosProperty.NAME, 0);
            description = WmiUtil.getString(win32BIOS, Win32Bios.BiosProperty.DESCRIPTION, 0);
            version = WmiUtil.getString(win32BIOS, Win32Bios.BiosProperty.VERSION, 0);
            releaseDate = WmiUtil.getDateString(win32BIOS, Win32Bios.BiosProperty.RELEASEDATE, 0);
        }
        return new Quintet<>(Util.isBlank(manufacturer) ? Constants.UNKNOWN : manufacturer,
            Util.isBlank(name) ? Constants.UNKNOWN : name,
            Util.isBlank(description) ? Constants.UNKNOWN : description,
            Util.isBlank(version) ? Constants.UNKNOWN : version,
            Util.isBlank(releaseDate) ? Constants.UNKNOWN : releaseDate);
    }

    @Override public String getManufacturer() {
        return manufNameDescVersRelease.get().getA();
    }

    @Override public String getName() {
        return manufNameDescVersRelease.get().getB();
    }

    @Override public String getDescription() {
        return manufNameDescVersRelease.get().getC();
    }

    @Override public String getVersion() {
        return manufNameDescVersRelease.get().getD();
    }

    @Override public String getReleaseDate() {
        return manufNameDescVersRelease.get().getE();
    }
}
