
package com.zestic.system.hardware.platform.unix.freebsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.*;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.BsdNetworkIF;
import com.zestic.system.hardware.platform.unix.UnixDisplay;

import java.util.List;

/*
 * FreeBsdHardwareAbstractionLayer class.
 */
@ThreadSafe public final class FreeBsdHardwareAbstractionLayer
    extends AbstractHardwareAbstractionLayer {

    @Override public ComputerSystem createComputerSystem() {
        return new FreeBsdComputerSystem();
    }

    @Override public GlobalMemory createMemory() {
        return new FreeBsdGlobalMemory();
    }

    @Override public CentralProcessor createProcessor() {
        return new FreeBsdCentralProcessor();
    }

    @Override public Sensors createSensors() {
        return new FreeBsdSensors();
    }

    @Override public List<PowerSource> getPowerSources() {
        return FreeBsdPowerSource.getPowerSources();
    }

    @Override public List<HWDiskStore> getDiskStores() {
        return FreeBsdHWDiskStore.getDisks();
    }

    @Override public List<Display> getDisplays() {
        return UnixDisplay.getDisplays();
    }

    @Override public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
        return BsdNetworkIF.getNetworks(includeLocalInterfaces);
    }

    @Override public List<UsbDevice> getUsbDevices(boolean tree) {
        return FreeBsdUsbDevice.getUsbDevices(tree);
    }

    @Override public List<SoundCard> getSoundCards() {
        return FreeBsdSoundCard.getSoundCards();
    }

    @Override public List<GraphicsCard> getGraphicsCards() {
        return FreeBsdGraphicsCard.getGraphicsCards();
    }
}
