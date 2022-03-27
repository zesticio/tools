
package com.zestic.system.hardware.platform.unix.aix;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Baseboard;
import com.zestic.system.hardware.Firmware;
import com.zestic.system.hardware.common.AbstractComputerSystem;
import com.zestic.system.util.Constants;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.Util;

import java.util.List;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * Hardware data obtained from lsattr
 */
@Immutable final class AixComputerSystem extends AbstractComputerSystem {

    private final Supplier<LsattrStrings> lsattrStrings = memoize(AixComputerSystem::readLsattr);
    private final Supplier<List<String>> lscfg;

    AixComputerSystem(Supplier<List<String>> lscfg) {
        this.lscfg = lscfg;
    }

    private static LsattrStrings readLsattr() {
        String fwVendor = "IBM";
        String fwVersion = null;
        String fwPlatformVersion = null;

        String manufacturer = fwVendor;
        String model = null;
        String serialNumber = null;
        String uuid = null;

        /*-
        fwversion       IBM,RG080425_d79e22_r                Firmware version and revision levels                False
        modelname       IBM,9114-275                         Machine name                                        False
        os_uuid         789f930f-b15c-4639-b842-b42603862704 N/A                                                 True
        rtasversion     1                                    Open Firmware RTAS version                          False
        systemid        IBM,0110ACFDE                        Hardware system identifier                          False
        */

        final String fwVersionMarker = "fwversion";
        final String modelMarker = "modelname";
        final String systemIdMarker = "systemid";
        final String uuidMarker = "os_uuid";
        final String fwPlatformVersionMarker = "Platform Firmware level is";

        for (final String checkLine : ExecutingCommand.runNative("lsattr -El sys0")) {
            if (checkLine.startsWith(fwVersionMarker)) {
                fwVersion = checkLine.split(fwVersionMarker)[1].trim();
                int comma = fwVersion.indexOf(',');
                if (comma > 0 && fwVersion.length() > comma) {
                    fwVendor = fwVersion.substring(0, comma);
                    fwVersion = fwVersion.substring(comma + 1);
                }
                fwVersion = ParseUtil.whitespaces.split(fwVersion)[0];
            } else if (checkLine.startsWith(modelMarker)) {
                model = checkLine.split(modelMarker)[1].trim();
                int comma = model.indexOf(',');
                if (comma > 0 && model.length() > comma) {
                    manufacturer = model.substring(0, comma);
                    model = model.substring(comma + 1);
                }
                model = ParseUtil.whitespaces.split(model)[0];
            } else if (checkLine.startsWith(systemIdMarker)) {
                serialNumber = checkLine.split(systemIdMarker)[1].trim();
                serialNumber = ParseUtil.whitespaces.split(serialNumber)[0];
            } else if (checkLine.startsWith(uuidMarker)) {
                uuid = checkLine.split(uuidMarker)[1].trim();
                uuid = ParseUtil.whitespaces.split(uuid)[0];
            }
        }
        for (final String checkLine : ExecutingCommand.runNative("lsmcode -c")) {
            /*-
             Platform Firmware level is 3F080425
             System Firmware level is RG080425_d79e22_regatta
             */
            if (checkLine.startsWith(fwPlatformVersionMarker)) {
                fwPlatformVersion = checkLine.split(fwPlatformVersionMarker)[1].trim();
                break;
            }
        }
        return new LsattrStrings(fwVendor, fwPlatformVersion, fwVersion, manufacturer, model,
            serialNumber, uuid);
    }

    @Override public String getManufacturer() {
        return lsattrStrings.get().manufacturer;
    }

    @Override public String getModel() {
        return lsattrStrings.get().model;
    }

    @Override public String getSerialNumber() {
        return lsattrStrings.get().serialNumber;
    }

    @Override public String getHardwareUUID() {
        return lsattrStrings.get().uuid;
    }

    @Override public Firmware createFirmware() {
        return new AixFirmware(lsattrStrings.get().biosVendor,
            lsattrStrings.get().biosPlatformVersion, lsattrStrings.get().biosVersion);
    }

    @Override public Baseboard createBaseboard() {
        return new AixBaseboard(lscfg);
    }


    private static final class LsattrStrings {
        private final String biosVendor;
        private final String biosPlatformVersion;
        private final String biosVersion;

        private final String manufacturer;
        private final String model;
        private final String serialNumber;
        private final String uuid;

        private LsattrStrings(String biosVendor, String biosPlatformVersion, String biosVersion,
            String manufacturer, String model, String serialNumber, String uuid) {
            this.biosVendor = Util.isBlank(biosVendor) ? Constants.UNKNOWN : biosVendor;
            this.biosPlatformVersion =
                Util.isBlank(biosPlatformVersion) ? Constants.UNKNOWN : biosPlatformVersion;
            this.biosVersion = Util.isBlank(biosVersion) ? Constants.UNKNOWN : biosVersion;

            this.manufacturer = Util.isBlank(manufacturer) ? Constants.UNKNOWN : manufacturer;
            this.model = Util.isBlank(model) ? Constants.UNKNOWN : model;
            this.serialNumber = Util.isBlank(serialNumber) ? Constants.UNKNOWN : serialNumber;
            this.uuid = Util.isBlank(uuid) ? Constants.UNKNOWN : uuid;
        }
    }
}
