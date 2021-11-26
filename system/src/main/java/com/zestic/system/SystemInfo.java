/*
 * MIT License
 *
 * Copyright (c) 2010 - 2021 The OSHI Project Contributors: https://github.com/oshi/oshi/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.zestic.system;

import com.sun.jna.Platform;
import com.zestic.system.hardware.HardwareAbstractionLayer;
import com.zestic.system.hardware.platform.linux.LinuxHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.mac.MacHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.aix.AixHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.freebsd.FreeBsdHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.openbsd.OpenBsdHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.solaris.SolarisHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.windows.WindowsHardwareAbstractionLayer;
import com.zestic.system.software.os.OperatingSystem;
import com.zestic.system.software.os.linux.LinuxOperatingSystem;
import com.zestic.system.software.os.mac.MacOperatingSystem;
import com.zestic.system.software.os.unix.aix.AixOperatingSystem;
import com.zestic.system.software.os.unix.freebsd.FreeBsdOperatingSystem;
import com.zestic.system.software.os.unix.openbsd.OpenBsdOperatingSystem;
import com.zestic.system.software.os.unix.solaris.SolarisOperatingSystem;
import com.zestic.system.software.os.windows.WindowsOperatingSystem;

import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.memoize;

/*
 * System information. This is the main entry point to com.zestic.system.
 * <p>
 * This object provides getters which instantiate the appropriate
 * platform-specific implementations of {@link OperatingSystem}
 * (software) and {@link HardwareAbstractionLayer} (hardware).
 */
public class SystemInfo {

    // The platform isn't going to change, and making this static enables easy
    // access from outside this class
    private static final PlatformEnum currentPlatform = PlatformEnum.getValue(Platform.getOSType());

    private static final String NOT_SUPPORTED = "Operating system not supported: ";

    private final Supplier<OperatingSystem> os = memoize(SystemInfo::createOperatingSystem);

    private final Supplier<HardwareAbstractionLayer> hardware = memoize(SystemInfo::createHardware);

    /*
     * Create a new instance of {@link SystemInfo}. This is the main entry point to
     * OSHI and provides access to cross-platform code.
     * <p>
     * Platform-specific Hardware and Software objects are retrieved via memoized
     * suppliers. To conserve memory at the cost of additional processing time,
     * create a new version of SystemInfo() for subsequent calls. To conserve
     * processing time at the cost of additional memory usage, re-use the same
     * {@link SystemInfo} object for future queries.
     */
    public SystemInfo() {
        // Intentionally empty, here to enable the constructor javadoc.
    }

    /*
     * Gets the {@link PlatformEnum} value representing this system.
     *
     * @return Returns the current platform
     */
    public static PlatformEnum getCurrentPlatform() {
        return currentPlatform;
    }

    private static OperatingSystem createOperatingSystem() {
        switch (currentPlatform) {
            case WINDOWS:
                return new WindowsOperatingSystem();
            case LINUX:
                return new LinuxOperatingSystem();
            case MACOS:
                return new MacOperatingSystem();
            case SOLARIS:
                return new SolarisOperatingSystem();
            case FREEBSD:
                return new FreeBsdOperatingSystem();
            case AIX:
                return new AixOperatingSystem();
            case OPENBSD:
                return new OpenBsdOperatingSystem();
            default:
                throw new UnsupportedOperationException(NOT_SUPPORTED + currentPlatform.getName());
        }
    }

    private static HardwareAbstractionLayer createHardware() {
        switch (currentPlatform) {
            case WINDOWS:
                return new WindowsHardwareAbstractionLayer();
            case LINUX:
                return new LinuxHardwareAbstractionLayer();
            case MACOS:
                return new MacHardwareAbstractionLayer();
            case SOLARIS:
                return new SolarisHardwareAbstractionLayer();
            case FREEBSD:
                return new FreeBsdHardwareAbstractionLayer();
            case AIX:
                return new AixHardwareAbstractionLayer();
            case OPENBSD:
                return new OpenBsdHardwareAbstractionLayer();
            default:
                throw new UnsupportedOperationException(NOT_SUPPORTED + currentPlatform.getName());
        }
    }

    /*
     * Creates a new instance of the appropriate platform-specific
     * {@link OperatingSystem}.
     *
     * @return A new instance of {@link OperatingSystem}.
     */
    public OperatingSystem getOperatingSystem() {
        return os.get();
    }

    /*
     * Creates a new instance of the appropriate platform-specific
     * {@link HardwareAbstractionLayer}.
     *
     * @return A new instance of {@link HardwareAbstractionLayer}.
     */
    public HardwareAbstractionLayer getHardware() {
        return hardware.get();
    }
}
