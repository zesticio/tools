
package com.zestic.system.hardware.platform.unix.openbsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.*;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.BsdNetworkIF;
import com.zestic.system.hardware.platform.unix.UnixDisplay;

import java.util.List;

/*
 * OpenBsdHardwareAbstractionLayer class.
 */
@ThreadSafe public final class OpenBsdHardwareAbstractionLayer
    extends AbstractHardwareAbstractionLayer {

    @Override public ComputerSystem createComputerSystem() {
        return new OpenBsdComputerSystem();
    }

    @Override public GlobalMemory createMemory() {
        return new OpenBsdGlobalMemory();
    }

    @Override public CentralProcessor createProcessor() {
        return new OpenBsdCentralProcessor();
    }

    @Override public Sensors createSensors() {
        return new OpenBsdSensors();
    }

    @Override public List<PowerSource> getPowerSources() {
        return OpenBsdPowerSource.getPowerSources();
    }

    @Override public List<HWDiskStore> getDiskStores() {
        return OpenBsdHWDiskStore.getDisks();
    }

    @Override public List<Display> getDisplays() {
        return UnixDisplay.getDisplays();
    }

    @Override public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
        return BsdNetworkIF.getNetworks(includeLocalInterfaces);
    }

    @Override public List<UsbDevice> getUsbDevices(boolean tree) {
        return OpenBsdUsbDevice.getUsbDevices(tree);
    }

    @Override public List<SoundCard> getSoundCards() {
        return OpenBsdSoundCard.getSoundCards();
    }

    @Override public List<GraphicsCard> getGraphicsCards() {
        return OpenBsdGraphicsCard.getGraphicsCards();
    }
}
