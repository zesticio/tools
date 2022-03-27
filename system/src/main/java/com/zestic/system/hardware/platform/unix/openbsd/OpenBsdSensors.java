
package com.zestic.system.hardware.platform.unix.openbsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractSensors;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Sensors
 */
@ThreadSafe final class OpenBsdSensors extends AbstractSensors {

    private final Supplier<Triplet<Double, int[], Double>> tempFanVolts =
        memoize(OpenBsdSensors::querySensors, defaultExpiration());

    private static Triplet<Double, int[], Double> querySensors() {
        double volts = 0d;
        List<Double> cpuTemps = new ArrayList<>();
        List<Double> allTemps = new ArrayList<>();
        List<Integer> fanRPMs = new ArrayList<>();
        for (String line : ExecutingCommand.runNative("systat -ab sensors")) {
            String[] split = ParseUtil.whitespaces.split(line);
            if (split.length > 1) {
                if (split[0].contains("cpu")) {
                    if (split[0].contains("temp0")) {
                        cpuTemps.add(ParseUtil.parseDoubleOrDefault(split[1], Double.NaN));
                    } else if (split[0].contains("volt0")) {
                        volts = ParseUtil.parseDoubleOrDefault(split[1], 0d);
                    }
                } else if (split[0].contains("temp0")) {
                    allTemps.add(ParseUtil.parseDoubleOrDefault(split[1], Double.NaN));
                } else if (split[0].contains("fan")) {
                    fanRPMs.add(ParseUtil.parseIntOrDefault(split[1], 0));
                }
            }
        }
        // Prefer cpu temps
        double temp = cpuTemps.isEmpty() ? listAverage(allTemps) : listAverage(cpuTemps);
        // Collect all fans
        int[] fans = new int[fanRPMs.size()];
        for (int i = 0; i < fans.length; i++) {
            fans[i] = fanRPMs.get(i);
        }
        return new Triplet<>(temp, fans, volts);
    }

    private static double listAverage(List<Double> doubles) {
        double sum = 0d;
        int count = 0;
        for (Double d : doubles) {
            if (!d.isNaN()) {
                sum += d;
                count++;
            }
        }
        return count > 0 ? sum / count : 0d;
    }

    @Override public double queryCpuTemperature() {
        return tempFanVolts.get().getA();
    }

    @Override public int[] queryFanSpeeds() {
        return tempFanVolts.get().getB();
    }

    @Override public double queryCpuVoltage() {
        return tempFanVolts.get().getC();
    }
}
