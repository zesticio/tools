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
package com.zestic.system.hardware.platform.windows;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.*;
import com.zestic.system.hardware.common.AbstractHardwareAbstractionLayer;

import java.util.List;

/*
 * WindowsHardwareAbstractionLayer class.
 */
@ThreadSafe public final class WindowsHardwareAbstractionLayer
    extends AbstractHardwareAbstractionLayer {

    @Override public ComputerSystem createComputerSystem() {
        return new WindowsComputerSystem();
    }

    @Override public GlobalMemory createMemory() {
        return new WindowsGlobalMemory();
    }

    @Override public CentralProcessor createProcessor() {
        return new WindowsCentralProcessor();
    }

    @Override public Sensors createSensors() {
        return new WindowsSensors();
    }

    @Override public List<PowerSource> getPowerSources() {
        return WindowsPowerSource.getPowerSources();
    }

    @Override public List<HWDiskStore> getDiskStores() {
        return WindowsHWDiskStore.getDisks();
    }

    @Override public List<LogicalVolumeGroup> getLogicalVolumeGroups() {
        return WindowsLogicalVolumeGroup.getLogicalVolumeGroups();
    }

    @Override public List<Display> getDisplays() {
        return WindowsDisplay.getDisplays();
    }

    @Override public List<NetworkIF> getNetworkIFs(boolean includeLocalInterfaces) {
        return WindowsNetworkIF.getNetworks(includeLocalInterfaces);
    }

    @Override public List<UsbDevice> getUsbDevices(boolean tree) {
        return WindowsUsbDevice.getUsbDevices(tree);
    }

    @Override public List<SoundCard> getSoundCards() {
        return WindowsSoundCard.getSoundCards();
    }

    @Override public List<GraphicsCard> getGraphicsCards() {
        return WindowsGraphicsCard.getGraphicsCards();
    }
}