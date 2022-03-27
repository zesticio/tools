
package com.zestic.system.hardware.platform.unix.aix;

import com.sun.jna.platform.unix.aix.Perfstat.perfstat_disk_t;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.unix.aix.Lscfg;
import com.zestic.system.driver.unix.aix.perfstat.PerfstatDisk;
import com.zestic.system.hardware.*;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.UnixDisplay;
import com.zestic.system.util.Memoizer;

import java.util.List;
import java.util.function.Supplier;

/*
 * AIXHardwareAbstractionLayer class.
 */
@ThreadSafe public final class AixHardwareAbstractionLayer
    extends AbstractHardwareAbstractionLayer {

    // Memoized hardware listing
    private final Supplier<List<String>> lscfg =
        Memoizer.memoize(Lscfg::queryAllDevices, Memoizer.defaultExpiration());
    // Memoized disk stats to pass to disk object(s)
    private final Supplier<perfstat_disk_t[]> diskStats =
        Memoizer.memoize(PerfstatDisk::queryDiskStats, Memoizer.defaultExpiration());

    @Override public ComputerSystem createComputerSystem() {
        return new AixComputerSystem(lscfg);
    }

    @Override public GlobalMemory createMemory() {
        return new AixGlobalMemory(lscfg);
    }

    @Override public CentralProcessor createProcessor() {
        return new AixCentralProcessor();
    }

    @Override public Sensors createSensors() {
        return new AixSensors(lscfg);
    }

    @Override public List<PowerSource> getPowerSources() {
        return AixPowerSource.getPowerSources();
    }

    @Override public List<HWDiskStore> getDiskStores() {
        return AixHWDiskStore.getDisks(diskStats);
    }

    @Override public List<Display> getDisplays() {
        return UnixDisplay.getDisplays();
    }

    @Override public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
        return AixNetworkIF.getNetworks(includeLocalInterfaces);
    }

    @Override public List<UsbDevice> getUsbDevices(boolean tree) {
        return AixUsbDevice.getUsbDevices(tree, lscfg);
    }

    @Override public List<SoundCard> getSoundCards() {
        return AixSoundCard.getSoundCards(lscfg);
    }

    @Override public List<GraphicsCard> getGraphicsCards() {
        return AixGraphicsCard.getGraphicsCards(lscfg);
    }
}
