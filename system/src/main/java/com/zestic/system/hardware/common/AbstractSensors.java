
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.Sensors;

import java.util.Arrays;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Sensors from WMI or Open Hardware Monitor
 */
@ThreadSafe public abstract class AbstractSensors implements Sensors {

    private final Supplier<Double> cpuTemperature =
        memoize(this::queryCpuTemperature, defaultExpiration());

    private final Supplier<int[]> fanSpeeds = memoize(this::queryFanSpeeds, defaultExpiration());

    private final Supplier<Double> cpuVoltage = memoize(this::queryCpuVoltage, defaultExpiration());

    @Override public double getCpuTemperature() {
        return cpuTemperature.get();
    }

    protected abstract double queryCpuTemperature();

    @Override public int[] getFanSpeeds() {
        return fanSpeeds.get();
    }

    protected abstract int[] queryFanSpeeds();

    @Override public double getCpuVoltage() {
        return cpuVoltage.get();
    }

    protected abstract double queryCpuVoltage();

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CPU Temperature=").append(getCpuTemperature()).append("°C, ");
        sb.append("Fan Speeds=").append(Arrays.toString(getFanSpeeds())).append(", ");
        sb.append("CPU Voltage=").append(getCpuVoltage());
        return sb.toString();
    }
}
