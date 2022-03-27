
package com.zestic.system.hardware.platform.unix.aix;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.driver.unix.aix.Lscfg;
import com.zestic.system.hardware.common.AbstractBaseboard;
import com.zestic.system.util.Constants;
import com.zestic.system.util.Util;
import com.zestic.system.util.tuples.Triplet;

import java.util.List;
import java.util.function.Supplier;

/*
 * Baseboard data obtained by lscfg
 */
@Immutable final class AixBaseboard extends AbstractBaseboard {

    private static final String IBM = "IBM";
    private final String model;
    private final String serialNumber;
    private final String version;

    AixBaseboard(Supplier<List<String>> lscfg) {
        Triplet<String, String, String> msv = Lscfg.queryBackplaneModelSerialVersion(lscfg.get());
        this.model = Util.isBlank(msv.getA()) ? Constants.UNKNOWN : msv.getA();
        this.serialNumber = Util.isBlank(msv.getB()) ? Constants.UNKNOWN : msv.getB();
        this.version = Util.isBlank(msv.getC()) ? Constants.UNKNOWN : msv.getC();
    }

    @Override public String getManufacturer() {
        return IBM;
    }

    @Override public String getModel() {
        return this.model;
    }

    @Override public String getSerialNumber() {
        return this.serialNumber;
    }

    @Override public String getVersion() {
        return this.version;
    }
}
