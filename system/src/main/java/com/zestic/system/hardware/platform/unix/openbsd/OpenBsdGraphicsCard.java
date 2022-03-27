
package com.zestic.system.hardware.platform.unix.openbsd;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.GraphicsCard;
import com.zestic.system.hardware.common.AbstractGraphicsCard;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ExecutingCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Graphics Card info obtained from pciconf
 */
@Immutable final class OpenBsdGraphicsCard extends AbstractGraphicsCard {

    private static final String PCI_CLASS_DISPLAY = "Class: 03 Display";
    private static final Pattern PCI_DUMP_HEADER = Pattern.compile(" \\d+:\\d+:\\d+: (.+)");

    /*
     * Constructor for OpenBsdGraphicsCard
     *
     * @param name        The name
     * @param deviceId    The device ID
     * @param vendor      The vendor
     * @param versionInfo The version info
     * @param vram        The VRAM
     */
    OpenBsdGraphicsCard(String name, String deviceId, String vendor, String versionInfo,
        long vram) {
        super(name, deviceId, vendor, versionInfo, vram);
    }

    /*
     * public method used by
     * {@link AbstractHardwareAbstractionLayer} to access the
     * graphics cards.
     *
     * @return List of
     * {@link com.zestic.system.hardware.platform.unix.freebsd.OpenBsdGraphicsCard}
     * objects.
     */
    public static List<GraphicsCard> getGraphicsCards() {
        List<GraphicsCard> cardList = new ArrayList<>();
        // Enumerate all devices and add if required
        List<String> devices = ExecutingCommand.runNative("pcidump -v");
        if (devices.isEmpty()) {
            return Collections.emptyList();
        }
        String name = "";
        String vendorId = "";
        String productId = "";
        boolean classCodeFound = false;
        String versionInfo = "";
        for (String line : devices) {
            Matcher m = PCI_DUMP_HEADER.matcher(line);
            if (m.matches()) {
                // Identifies start of a new device. Save previous if it's a graphics card
                if (classCodeFound) {
                    cardList.add(new OpenBsdGraphicsCard(name.isEmpty() ? Constants.UNKNOWN : name,
                        productId.isEmpty() ? "0x0000" : productId,
                        vendorId.isEmpty() ? "0x0000" : vendorId,
                        versionInfo.isEmpty() ? Constants.UNKNOWN : versionInfo, 0L));
                }
                // Device name is the captured pattern
                name = m.group(1);
                // Reset values
                vendorId = "";
                productId = "";
                classCodeFound = false;
                versionInfo = "";
            } else {
                int idx;
                // Look for:
                // 0x0000: Vendor ID: 1ab8, Product ID: 4005
                // 0x0008: Class: 03 Display, Subclass: 00 VGA
                // ....... Interface: 00, Revision: 00
                if (!classCodeFound) {
                    idx = line.indexOf("Vendor ID: ");
                    if (idx >= 0 && line.length() >= idx + 15) {
                        vendorId = "0x" + line.substring(idx + 11, idx + 15);
                    }
                    idx = line.indexOf("Product ID: ");
                    if (idx >= 0 && line.length() >= idx + 16) {
                        productId = "0x" + line.substring(idx + 12, idx + 16);
                    }
                    if (line.contains(PCI_CLASS_DISPLAY)) {
                        classCodeFound = true;
                    }
                } else if (versionInfo.isEmpty()) {
                    idx = line.indexOf("Revision: ");
                    if (idx >= 0) {
                        versionInfo = line.substring(idx);
                    }
                }
            }
        }
        // In case we reached end before saving
        if (classCodeFound) {
            cardList.add(new OpenBsdGraphicsCard(name.isEmpty() ? Constants.UNKNOWN : name,
                productId.isEmpty() ? "0x0000" : productId,
                vendorId.isEmpty() ? "0x0000" : vendorId,
                versionInfo.isEmpty() ? Constants.UNKNOWN : versionInfo, 0L));
        }
        return cardList;
    }
}
