
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.driver.windows.wmi.Win32BaseBoard;
import com.zestic.system.driver.windows.wmi.Win32BaseBoard.BaseBoardProperty;
import com.zestic.system.hardware.common.AbstractBaseboard;
import com.zestic.system.util.Constants;
import com.zestic.system.util.Util;
import com.zestic.system.util.platform.windows.WmiUtil;
import com.zestic.system.util.tuples.Quartet;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * Baseboard data obtained from WMI
 */
@Immutable final class WindowsBaseboard extends AbstractBaseboard {

    private final Supplier<Quartet<String, String, String, String>> manufModelVersSerial =
        memoize(WindowsBaseboard::queryManufModelVersSerial);

    private static Quartet<String, String, String, String> queryManufModelVersSerial() {
        String manufacturer = null;
        String model = null;
        String version = null;
        String serialNumber = null;
        WmiResult<BaseBoardProperty> win32BaseBoard = Win32BaseBoard.queryBaseboardInfo();
        if (win32BaseBoard.getResultCount() > 0) {
            manufacturer = WmiUtil.getString(win32BaseBoard, BaseBoardProperty.MANUFACTURER, 0);
            model = WmiUtil.getString(win32BaseBoard, BaseBoardProperty.MODEL, 0);
            version = WmiUtil.getString(win32BaseBoard, BaseBoardProperty.VERSION, 0);
            serialNumber = WmiUtil.getString(win32BaseBoard, BaseBoardProperty.SERIALNUMBER, 0);
        }
        return new Quartet<>(Util.isBlank(manufacturer) ? Constants.UNKNOWN : manufacturer,
            Util.isBlank(model) ? Constants.UNKNOWN : model,
            Util.isBlank(version) ? Constants.UNKNOWN : version,
            Util.isBlank(serialNumber) ? Constants.UNKNOWN : serialNumber);
    }

    @Override public String getManufacturer() {
        return manufModelVersSerial.get().getA();
    }

    @Override public String getModel() {
        return manufModelVersSerial.get().getB();
    }

    @Override public String getVersion() {
        return manufModelVersSerial.get().getC();
    }

    @Override public String getSerialNumber() {
        return manufModelVersSerial.get().getD();
    }
}
