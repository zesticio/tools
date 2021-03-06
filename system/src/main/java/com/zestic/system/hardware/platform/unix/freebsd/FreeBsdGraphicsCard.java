
package com.zestic.system.hardware.platform.unix.freebsd;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.GraphicsCard;
import com.zestic.system.hardware.common.AbstractGraphicsCard;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * Graphics Card info obtained from pciconf
 */
@Immutable final class FreeBsdGraphicsCard extends AbstractGraphicsCard {

    private static final String PCI_CLASS_DISPLAY = "0x03";

    /*
     * Constructor for FreeBsdGraphicsCard
     *
     * @param name        The name
     * @param deviceId    The device ID
     * @param vendor      The vendor
     * @param versionInfo The version info
     * @param vram        The VRAM
     */
    FreeBsdGraphicsCard(String name, String deviceId, String vendor, String versionInfo,
        long vram) {
        super(name, deviceId, vendor, versionInfo, vram);
    }

    /*
     * public method used by
     * {@link AbstractHardwareAbstractionLayer} to access the
     * graphics cards.
     *
     * @return List of
     * {@link FreeBsdGraphicsCard}
     * objects.
     */
    public static List<GraphicsCard> getGraphicsCards() {
        List<GraphicsCard> cardList = new ArrayList<>();
        // Enumerate all devices and add if required
        List<String> devices = ExecutingCommand.runNative("pciconf -lv");
        if (devices.isEmpty()) {
            return Collections.emptyList();
        }
        String name = Constants.UNKNOWN;
        String vendorId = Constants.UNKNOWN;
        String productId = Constants.UNKNOWN;
        String classCode = "";
        String versionInfo = Constants.UNKNOWN;
        for (String line : devices) {
            if (line.contains("class=0x")) {
                // Identifies start of a new device. Save previous if it's a graphics card
                if (PCI_CLASS_DISPLAY.equals(classCode)) {
                    cardList.add(new FreeBsdGraphicsCard(name.isEmpty() ? Constants.UNKNOWN : name,
                        productId.isEmpty() ? Constants.UNKNOWN : productId,
                        vendorId.isEmpty() ? Constants.UNKNOWN : vendorId,
                        versionInfo.isEmpty() ? Constants.UNKNOWN : versionInfo, 0L));
                }
                // Parse this line
                String[] split = ParseUtil.whitespaces.split(line);
                for (String s : split) {
                    String[] keyVal = s.split("=");
                    if (keyVal.length > 1) {
                        if (keyVal[0].equals("class") && keyVal[1].length() >= 4) {
                            // class=0x030000
                            classCode = keyVal[1].substring(0, 4);
                        } else if (keyVal[0].equals("chip") && keyVal[1].length() >= 10) {
                            // chip=0x3ea08086
                            productId = keyVal[1].substring(0, 6);
                            vendorId = "0x" + keyVal[1].substring(6, 10);
                        } else if (keyVal[0].contains("rev")) {
                            // rev=0x00
                            versionInfo = s;
                        }
                    }
                }
                // Reset name
                name = Constants.UNKNOWN;
            } else {
                String[] split = line.trim().split("=", 2);
                if (split.length == 2) {
                    String key = split[0].trim();
                    if (key.equals("vendor")) {
                        vendorId = ParseUtil.getSingleQuoteStringValue(line) + (vendorId.equals(
                            Constants.UNKNOWN) ? "" : " (" + vendorId + ")");
                    } else if (key.equals("device")) {
                        name = ParseUtil.getSingleQuoteStringValue(line);
                    }
                }
            }
        }
        // In case we reached end before saving
        if (PCI_CLASS_DISPLAY.equals(classCode)) {
            cardList.add(new FreeBsdGraphicsCard(name.isEmpty() ? Constants.UNKNOWN : name,
                productId.isEmpty() ? Constants.UNKNOWN : productId,
                vendorId.isEmpty() ? Constants.UNKNOWN : vendorId,
                versionInfo.isEmpty() ? Constants.UNKNOWN : versionInfo, 0L));
        }
        return cardList;
    }
}
