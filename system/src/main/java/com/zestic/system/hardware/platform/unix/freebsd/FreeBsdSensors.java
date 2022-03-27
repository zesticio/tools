
package com.zestic.system.hardware.platform.unix.freebsd;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.unix.LibCAPI.size_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.common.AbstractSensors;
import com.zestic.system.jna.platform.unix.freebsd.FreeBsdLibc;

/*
 * Sensors from coretemp
 */
@ThreadSafe final class FreeBsdSensors extends AbstractSensors {

    /*
     * If user has loaded coretemp module via kldload coretemp, sysctl call will
     * return temperature
     *
     * @return Tempurature if successful, otherwise NaN
     */
    private static double queryKldloadCoretemp() {
        String name = "dev.cpu.%d.temperature";
        size_t.ByReference size = new size_t.ByReference(new size_t(FreeBsdLibc.INT_SIZE));
        Pointer p = new Memory(size.longValue());
        int cpu = 0;
        double sumTemp = 0d;
        while (0 == FreeBsdLibc.INSTANCE.sysctlbyname(String.format(name, cpu), p, size, null,
            size_t.ZERO)) {
            sumTemp += p.getInt(0) / 10d - 273.15;
            cpu++;
        }
        return cpu > 0 ? sumTemp / cpu : Double.NaN;
    }

    @Override public double queryCpuTemperature() {
        return queryKldloadCoretemp();
    }

    @Override public int[] queryFanSpeeds() {
        // Nothing known on FreeBSD for this.
        return new int[0];
    }

    @Override public double queryCpuVoltage() {
        // Nothing known on FreeBSD for this.
        return 0d;
    }
}
