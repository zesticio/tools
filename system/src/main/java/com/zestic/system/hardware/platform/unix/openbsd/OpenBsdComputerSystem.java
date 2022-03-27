
package com.zestic.system.hardware.platform.unix.openbsd;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Baseboard;
import com.zestic.system.hardware.Firmware;
import com.zestic.system.hardware.common.AbstractComputerSystem;
import com.zestic.system.hardware.platform.unix.UnixBaseboard;
import com.zestic.system.util.Constants;
import com.zestic.system.util.platform.unix.openbsd.OpenBsdSysctlUtil;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * OpenBSD ComputerSystem implementation
 */
@Immutable public class OpenBsdComputerSystem extends AbstractComputerSystem {

    private final Supplier<String> manufacturer = memoize(OpenBsdComputerSystem::queryManufacturer);

    private final Supplier<String> model = memoize(OpenBsdComputerSystem::queryModel);

    private final Supplier<String> serialNumber = memoize(OpenBsdComputerSystem::querySerialNumber);

    private final Supplier<String> uuid = memoize(OpenBsdComputerSystem::queryUUID);

    private static String queryManufacturer() {
        return OpenBsdSysctlUtil.sysctl("hw.vendor", Constants.UNKNOWN);
    }

    private static String queryModel() {
        return OpenBsdSysctlUtil.sysctl("hw.version", Constants.UNKNOWN);
    }

    private static String querySerialNumber() {
        return OpenBsdSysctlUtil.sysctl("hw.serialno", Constants.UNKNOWN);
    }

    private static String queryUUID() {
        return OpenBsdSysctlUtil.sysctl("hw.uuid", Constants.UNKNOWN);
    }

    @Override public String getManufacturer() {
        return manufacturer.get();
    }

    @Override public String getModel() {
        return model.get();
    }

    @Override public String getSerialNumber() {
        return serialNumber.get();
    }

    @Override public String getHardwareUUID() {
        return uuid.get();
    }

    @Override protected Firmware createFirmware() {
        return new OpenBsdFirmware();
    }

    @Override protected Baseboard createBaseboard() {
        return new UnixBaseboard(manufacturer.get(), model.get(), serialNumber.get(),
            OpenBsdSysctlUtil.sysctl("hw.product", Constants.UNKNOWN));
    }
}
