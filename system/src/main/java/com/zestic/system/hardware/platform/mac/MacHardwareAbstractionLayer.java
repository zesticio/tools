
package com.zestic.system.hardware.platform.mac;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.*;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;

import java.util.List;

/*
 * MacHardwareAbstractionLayer class.
 */
@ThreadSafe public final class MacHardwareAbstractionLayer
    extends AbstractHardwareAbstractionLayer {

    @Override public ComputerSystem createComputerSystem() {
        return new MacComputerSystem();
    }

    @Override public GlobalMemory createMemory() {
        return new MacGlobalMemory();
    }

    @Override public CentralProcessor createProcessor() {
        return new MacCentralProcessor();
    }

    @Override public Sensors createSensors() {
        return new MacSensors();
    }

    @Override public List<PowerSource> getPowerSources() {
        return MacPowerSource.getPowerSources();
    }

    @Override public List<HWDiskStore> getDiskStores() {
        return MacHWDiskStore.getDisks();
    }

    @Override public List<LogicalVolumeGroup> getLogicalVolumeGroups() {
        return MacLogicalVolumeGroup.getLogicalVolumeGroups();
    }

    @Override public List<Display> getDisplays() {
        return MacDisplay.getDisplays();
    }

    @Override public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
        return MacNetworkIF.getNetworks(includeLocalInterfaces);
    }

    @Override public List<UsbDevice> getUsbDevices(boolean tree) {
        return MacUsbDevice.getUsbDevices(tree);
    }

    @Override public List<SoundCard> getSoundCards() {
        return MacSoundCard.getSoundCards();
    }

    @Override public List<GraphicsCard> getGraphicsCards() {
        return MacGraphicsCard.getGraphicsCards();
    }
}
