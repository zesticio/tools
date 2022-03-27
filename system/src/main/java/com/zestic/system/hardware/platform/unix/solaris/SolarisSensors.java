
package com.zestic.system.hardware.platform.unix.solaris;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractSensors;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.ArrayList;
import java.util.List;

/*
 * Sensors from prtpicl
 */
@ThreadSafe final class SolarisSensors extends AbstractSensors {

    @Override public double queryCpuTemperature() {
        double maxTemp = 0d;
        // Return max found temp
        for (String line : ExecutingCommand.runNative(
            "/usr/sbin/prtpicl -v -c temperature-sensor")) {
            if (line.trim().startsWith("Temperature:")) {
                int temp = ParseUtil.parseLastInt(line, 0);
                if (temp > maxTemp) {
                    maxTemp = temp;
                }
            }
        }
        // If it's in millidegrees:
        if (maxTemp > 1000) {
            maxTemp /= 1000;
        }
        return maxTemp;
    }

    @Override public int[] queryFanSpeeds() {
        List<Integer> speedList = new ArrayList<>();
        // Return max found temp
        for (String line : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c fan")) {
            if (line.trim().startsWith("Speed:")) {
                speedList.add(ParseUtil.parseLastInt(line, 0));
            }
        }
        int[] fans = new int[speedList.size()];
        for (int i = 0; i < speedList.size(); i++) {
            fans[i] = speedList.get(i);
        }
        return fans;
    }

    @Override public double queryCpuVoltage() {
        double voltage = 0d;
        for (String line : ExecutingCommand.runNative("/usr/sbin/prtpicl -v -c voltage-sensor")) {
            if (line.trim().startsWith("Voltage:")) {
                voltage = ParseUtil.parseDoubleOrDefault(line.replace("Voltage:", "").trim(), 0d);
                break;
            }
        }
        return voltage;
    }
}
