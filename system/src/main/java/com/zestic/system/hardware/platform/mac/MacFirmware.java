
package com.zestic.system.hardware.platform.mac;

import com.sun.jna.Native;
import com.sun.jna.platform.mac.IOKit.IOIterator;
import com.sun.jna.platform.mac.IOKit.IORegistryEntry;
import com.sun.jna.platform.mac.IOKitUtil;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.common.AbstractFirmware;
import com.zestic.system.util.Constants;
import com.zestic.system.util.Util;
import com.zestic.system.util.tuples.Quintet;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * Firmware data obtained from ioreg.
 */
@Immutable final class MacFirmware extends AbstractFirmware {

    private final Supplier<Quintet<String, String, String, String, String>>
        manufNameDescVersRelease = memoize(MacFirmware::queryEfi);

    private static Quintet<String, String, String, String, String> queryEfi() {
        String manufacturer = null;
        String name = null;
        String description = null;
        String version = null;
        String releaseDate = null;

        IORegistryEntry platformExpert = IOKitUtil.getMatchingService("IOPlatformExpertDevice");
        byte[] data;
        if (platformExpert != null) {
            IOIterator iter = platformExpert.getChildIterator("IODeviceTree");
            if (iter != null) {
                IORegistryEntry entry = iter.next();
                while (entry != null) {
                    switch (entry.getName()) {
                        case "rom":
                            data = entry.getByteArrayProperty("vendor");
                            if (data != null) {
                                manufacturer = Native.toString(data, StandardCharsets.UTF_8);
                            }
                            data = entry.getByteArrayProperty("version");
                            if (data != null) {
                                version = Native.toString(data, StandardCharsets.UTF_8);
                            }
                            data = entry.getByteArrayProperty("release-date");
                            if (data != null) {
                                releaseDate = Native.toString(data, StandardCharsets.UTF_8);
                            }
                            break;
                        case "chosen":
                            data = entry.getByteArrayProperty("booter-name");
                            if (data != null) {
                                name = Native.toString(data, StandardCharsets.UTF_8);
                            }
                            break;
                        case "efi":
                            data = entry.getByteArrayProperty("firmware-abi");
                            if (data != null) {
                                description = Native.toString(data, StandardCharsets.UTF_8);
                            }
                            break;
                        default:
                            if (Util.isBlank(name)) {
                                name = entry.getStringProperty("IONameMatch");
                            }
                            break;
                    }
                    entry.release();
                    entry = iter.next();
                }
                iter.release();
            }
            if (Util.isBlank(manufacturer)) {
                data = platformExpert.getByteArrayProperty("manufacturer");
                if (data != null) {
                    manufacturer = Native.toString(data, StandardCharsets.UTF_8);
                }
            }
            if (Util.isBlank(version)) {
                data = platformExpert.getByteArrayProperty("target-type");
                if (data != null) {
                    version = Native.toString(data, StandardCharsets.UTF_8);
                }
            }
            if (Util.isBlank(name)) {
                data = platformExpert.getByteArrayProperty("device_type");
                if (data != null) {
                    name = Native.toString(data, StandardCharsets.UTF_8);
                }
            }
            platformExpert.release();
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
