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
package com.zestic.system.hardware.platform.linux;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.*;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;
import com.zestic.system.hardware.platform.unix.UnixDisplay;

import java.util.List;

/*
 * LinuxHardwareAbstractionLayer class.
 */
@ThreadSafe public final class LinuxHardwareAbstractionLayer
    extends AbstractHardwareAbstractionLayer {

    @Override public ComputerSystem createComputerSystem() {
        return new LinuxComputerSystem();
    }

    @Override public GlobalMemory createMemory() {
        return new LinuxGlobalMemory();
    }

    @Override public CentralProcessor createProcessor() {
        return new LinuxCentralProcessor();
    }

    @Override public Sensors createSensors() {
        return new LinuxSensors();
    }

    @Override public List<PowerSource> getPowerSources() {
        return LinuxPowerSource.getPowerSources();
    }

    @Override public List<HWDiskStore> getDiskStores() {
        return LinuxHWDiskStore.getDisks();
    }

    @Override public List<LogicalVolumeGroup> getLogicalVolumeGroups() {
        return LinuxLogicalVolumeGroup.getLogicalVolumeGroups();
    }

    @Override public List<Display> getDisplays() {
        return UnixDisplay.getDisplays();
    }

    @Override public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
        return LinuxNetworkIF.getNetworks(includeLocalInterfaces);
    }

    @Override public List<UsbDevice> getUsbDevices(boolean tree) {
        return LinuxUsbDevice.getUsbDevices(tree);
    }

    @Override public List<SoundCard> getSoundCards() {
        return LinuxSoundCard.getSoundCards();
    }

    @Override public List<GraphicsCard> getGraphicsCards() {
        return LinuxGraphicsCard.getGraphicsCards();
    }
}
