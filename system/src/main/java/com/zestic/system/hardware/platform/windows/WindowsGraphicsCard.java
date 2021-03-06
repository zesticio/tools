
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.VersionHelpers;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.driver.windows.wmi.Win32VideoController;
import com.zestic.system.hardware.GraphicsCard;
import com.zestic.system.hardware.common.AbstractGraphicsCard;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.Util;
import com.zestic.system.util.platform.windows.WmiUtil;
import com.zestic.system.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.List;

/*
 * Graphics Card obtained from WMI
 */
@Immutable final class WindowsGraphicsCard extends AbstractGraphicsCard {

    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();

    /*
     * Constructor for WindowsGraphicsCard
     *
     * @param name        The name
     * @param deviceId    The device ID
     * @param vendor      The vendor
     * @param versionInfo The version info
     * @param vram        The VRAM
     */
    WindowsGraphicsCard(String name, String deviceId, String vendor, String versionInfo,
        long vram) {
        super(name, deviceId, vendor, versionInfo, vram);
    }

    /*
     * public method used by
     * {@link AbstractHardwareAbstractionLayer} to access the
     * graphics cards.
     *
     * @return List of {@link WindowsGraphicsCard}
     * objects.
     */
    public static List<GraphicsCard> getGraphicsCards() {
        List<GraphicsCard> cardList = new ArrayList<>();
        if (IS_VISTA_OR_GREATER) {
            WmiResult<Win32VideoController.VideoControllerProperty> cards =
                Win32VideoController.queryVideoController();
            for (int index = 0; index < cards.getResultCount(); index++) {
                String name =
                    WmiUtil.getString(cards, Win32VideoController.VideoControllerProperty.NAME,
                        index);
                Triplet<String, String, String> idPair =
                    ParseUtil.parseDeviceIdToVendorProductSerial(WmiUtil.getString(cards,
                        Win32VideoController.VideoControllerProperty.PNPDEVICEID, index));
                String deviceId = idPair == null ? Constants.UNKNOWN : idPair.getB();
                String vendor = WmiUtil.getString(cards,
                    Win32VideoController.VideoControllerProperty.ADAPTERCOMPATIBILITY, index);
                if (idPair != null) {
                    if (Util.isBlank(vendor)) {
                        deviceId = idPair.getA();
                    } else {
                        vendor = vendor + " (" + idPair.getA() + ")";
                    }
                }
                String versionInfo = WmiUtil.getString(cards,
                    Win32VideoController.VideoControllerProperty.DRIVERVERSION, index);
                if (!Util.isBlank(versionInfo)) {
                    versionInfo = "DriverVersion=" + versionInfo;
                } else {
                    versionInfo = Constants.UNKNOWN;
                }
                long vram = WmiUtil.getUint32asLong(cards,
                    Win32VideoController.VideoControllerProperty.ADAPTERRAM, index);
                cardList.add(
                    new WindowsGraphicsCard(Util.isBlank(name) ? Constants.UNKNOWN : name, deviceId,
                        Util.isBlank(vendor) ? Constants.UNKNOWN : vendor, versionInfo, vram));
            }
        }
        return cardList;
    }
}
