
package com.zestic.system.hardware.platform.unix.aix;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractSensors;

import java.util.List;
import java.util.function.Supplier;

/*
 * Sensors not available except counting fans from lscfg
 */
@ThreadSafe final class AixSensors extends AbstractSensors {

    private final Supplier<List<String>> lscfg;

    AixSensors(Supplier<List<String>> lscfg) {
        this.lscfg = lscfg;
    }

    @Override public double queryCpuTemperature() {
        // Not available in general without specialized software
        return 0d;
    }

    @Override public int[] queryFanSpeeds() {
        // Speeds are not available in general without specialized software
        // We can count fans from lscfg and return an appropriate sized array of zeroes.
        int fans = 0;
        for (String s : lscfg.get()) {
            if (s.contains("Air Mover")) {
                fans++;
            }
        }
        return new int[fans];
    }

    @Override public double queryCpuVoltage() {
        // Not available in general without specialized software
        return 0d;
    }
}
