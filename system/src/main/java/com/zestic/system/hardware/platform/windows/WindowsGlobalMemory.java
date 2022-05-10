
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Psapi;
import com.sun.jna.platform.win32.Psapi.PERFORMANCE_INFORMATION;
import com.sun.jna.platform.win32.VersionHelpers;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.driver.windows.wmi.Win32PhysicalMemory;
import com.zestic.system.driver.windows.wmi.Win32PhysicalMemory.PhysicalMemoryProperty;
import com.zestic.system.driver.windows.wmi.Win32PhysicalMemory.PhysicalMemoryPropertyWin8;
import com.zestic.system.hardware.PhysicalMemory;
import com.zestic.system.hardware.VirtualMemory;
import com.zestic.system.hardware.common.AbstractGlobalMemory;
import com.zestic.system.util.platform.windows.WmiUtil;
import com.zestic.system.util.tuples.Triplet;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static com.zestic.system.util.Memoizer.defaultExpiration;
import static com.zestic.system.util.Memoizer.memoize;

/*
 * Memory obtained by Performance Info.
 */
@ThreadSafe
final class WindowsGlobalMemory extends AbstractGlobalMemory {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(WindowsVirtualMemory.class);

    private static final boolean IS_WINDOWS10_OR_GREATER = VersionHelpers.IsWindows10OrGreater();

    private final Supplier<Triplet<Long, Long, Long>> availTotalSize =
            memoize(WindowsGlobalMemory::readPerfInfo, defaultExpiration());

    private final Supplier<VirtualMemory> vm = memoize(this::createVirtualMemory);

    /*
     * Convert memory type number to a human readable string
     *
     * @param type The memory type
     * @return A string describing the type
     */
    private static String memoryType(int type) {
        switch (type) {
            case 1:
                return "Other";
            case 2:
                return "DRAM";
            case 3:
                return "Synchronous DRAM";
            case 4:
                return "Cache DRAM";
            case 5:
                return "EDO";
            case 6:
                return "EDRAM";
            case 7:
                return "VRAM";
            case 8:
                return "SRAM";
            case 9:
                return "RAM";
            case 10:
                return "ROM";
            case 11:
                return "Flash";
            case 12:
                return "EEPROM";
            case 13:
                return "FEPROM";
            case 14:
                return "EPROM";
            case 15:
                return "CDRAM";
            case 16:
                return "3DRAM";
            case 17:
                return "SDRAM";
            case 18:
                return "SGRAM";
            case 19:
                return "RDRAM";
            case 20:
                return "DDR";
            case 21:
                return "DDR2";
            case 22:
                return "DDR2-FB-DIMM";
            case 24:
                return "DDR3";
            case 25:
                return "FBD2";
            default:
                return "Unknown";
        }
    }

    /*
     * Convert SMBIOS type number to a human readable string
     *
     * @param type The SMBIOS type
     * @return A string describing the type
     */
    private static String smBiosMemoryType(int type) {
        // https://www.dmtf.org/sites/default/files/standards/documents/DSP0134_3.2.0.pdf
        // table 76
        switch (type) {
            case 0x01:
                return "Other";
            case 0x03:
                return "DRAM";
            case 0x04:
                return "EDRAM";
            case 0x05:
                return "VRAM";
            case 0x06:
                return "SRAM";
            case 0x07:
                return "RAM";
            case 0x08:
                return "ROM";
            case 0x09:
                return "FLASH";
            case 0x0A:
                return "EEPROM";
            case 0x0B:
                return "FEPROM";
            case 0x0C:
                return "EPROM";
            case 0x0D:
                return "CDRAM";
            case 0x0E:
                return "3DRAM";
            case 0x0F:
                return "SDRAM";
            case 0x10:
                return "SGRAM";
            case 0x11:
                return "RDRAM";
            case 0x12:
                return "DDR";
            case 0x13:
                return "DDR2";
            case 0x14:
                return "DDR2 FB-DIMM";
            case 0x18:
                return "DDR3";
            case 0x19:
                return "FBD2";
            case 0x1A:
                return "DDR4";
            case 0x1B:
                return "LPDDR";
            case 0x1C:
                return "LPDDR2";
            case 0x1D:
                return "LPDDR3";
            case 0x1E:
                return "LPDDR4";
            case 0x1F:
                return "Logical non-volatile device";
            case 0x02:
            default:
                return "Unknown";
        }
    }

    private static Triplet<Long, Long, Long> readPerfInfo() {
        PERFORMANCE_INFORMATION performanceInfo = new PERFORMANCE_INFORMATION();
        if (!Psapi.INSTANCE.GetPerformanceInfo(performanceInfo, performanceInfo.size())) {
            logger.error("Failed to get Performance Info. Error code: {}" +
                    Kernel32.INSTANCE.GetLastError());
            return new Triplet<>(0L, 0L, 4098L);
        }
        long pageSize = performanceInfo.PageSize.longValue();
        long memAvailable = pageSize * performanceInfo.PhysicalAvailable.longValue();
        long memTotal = pageSize * performanceInfo.PhysicalTotal.longValue();
        return new Triplet<>(memAvailable, memTotal, pageSize);
    }

    @Override
    public long getAvailable() {
        return availTotalSize.get().getA();
    }

    @Override
    public long getTotal() {
        return availTotalSize.get().getB();
    }

    @Override
    public long getPageSize() {
        return availTotalSize.get().getC();
    }

    @Override
    public VirtualMemory getVirtualMemory() {
        return vm.get();
    }

    private VirtualMemory createVirtualMemory() {
        return new WindowsVirtualMemory(this);
    }

    @Override
    public List<PhysicalMemory> getPhysicalMemory() {
        List<PhysicalMemory> physicalMemoryList = new ArrayList<>();
        if (IS_WINDOWS10_OR_GREATER) {
            WmiResult<PhysicalMemoryProperty> bankMap = Win32PhysicalMemory.queryphysicalMemory();
            for (int index = 0; index < bankMap.getResultCount(); index++) {
                String bankLabel =
                        WmiUtil.getString(bankMap, PhysicalMemoryProperty.BANKLABEL, index);
                long capacity = WmiUtil.getUint64(bankMap, PhysicalMemoryProperty.CAPACITY, index);
                long speed =
                        WmiUtil.getUint32(bankMap, PhysicalMemoryProperty.SPEED, index) * 1_000_000L;
                String manufacturer =
                        WmiUtil.getString(bankMap, PhysicalMemoryProperty.MANUFACTURER, index);
                String memoryType = smBiosMemoryType(
                        WmiUtil.getUint32(bankMap, PhysicalMemoryProperty.SMBIOSMEMORYTYPE, index));
                physicalMemoryList.add(
                        new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
            }
        } else {
            WmiResult<PhysicalMemoryPropertyWin8> bankMap =
                    Win32PhysicalMemory.queryphysicalMemoryWin8();
            for (int index = 0; index < bankMap.getResultCount(); index++) {
                String bankLabel =
                        WmiUtil.getString(bankMap, PhysicalMemoryPropertyWin8.BANKLABEL, index);
                long capacity =
                        WmiUtil.getUint64(bankMap, PhysicalMemoryPropertyWin8.CAPACITY, index);
                long speed = WmiUtil.getUint32(bankMap, PhysicalMemoryPropertyWin8.SPEED, index)
                        * 1_000_000L;
                String manufacturer =
                        WmiUtil.getString(bankMap, PhysicalMemoryPropertyWin8.MANUFACTURER, index);
                String memoryType = memoryType(
                        WmiUtil.getUint16(bankMap, PhysicalMemoryPropertyWin8.MEMORYTYPE, index));
                physicalMemoryList.add(
                        new PhysicalMemory(bankLabel, capacity, speed, manufacturer, memoryType));
            }
        }
        return physicalMemoryList;
    }
}
