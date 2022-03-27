
package com.zestic.system.hardware.platform.windows;

import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.SetupApi.SP_DEVICE_INTERFACE_DATA;
import com.sun.jna.platform.win32.SetupApi.SP_DEVINFO_DATA;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.ptr.IntByReference;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.hardware.Display;
import com.zestic.system.hardware.common.AbstractDisplay;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;

import java.util.ArrayList;
import java.util.List;

/*
 * A Display
 */
@Immutable final class WindowsDisplay extends AbstractDisplay {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.LogManager.getLogger(AixNetworkIF.class);

    private static final SetupApi SU = SetupApi.INSTANCE;
    private static final Advapi32 ADV = Advapi32.INSTANCE;

    private static final Guid.GUID GUID_DEVINTERFACE_MONITOR =
        new Guid.GUID("E6F07B5F-EE97-4a90-B076-33F57BF4EAA7");

    /*
     * Constructor for WindowsDisplay.
     *
     * @param edid a byte array representing a display EDID
     */
    WindowsDisplay(byte[] edid) {
        super(edid);
        LOG.debug("Initialized WindowsDisplay");
    }

    /*
     * Gets Display Information
     *
     * @return An array of Display objects representing monitors, etc.
     */
    public static List<Display> getDisplays() {
        List<Display> displays = new ArrayList<>();

        WinNT.HANDLE hDevInfo = SU.SetupDiGetClassDevs(GUID_DEVINTERFACE_MONITOR, null, null,
            SetupApi.DIGCF_PRESENT | SetupApi.DIGCF_DEVICEINTERFACE);
        if (!hDevInfo.equals(WinBase.INVALID_HANDLE_VALUE)) {
            SP_DEVICE_INTERFACE_DATA deviceInterfaceData = new SetupApi.SP_DEVICE_INTERFACE_DATA();
            deviceInterfaceData.cbSize = deviceInterfaceData.size();

            // build a DevInfo Data structure
            SP_DEVINFO_DATA info = new SetupApi.SP_DEVINFO_DATA();

            for (int memberIndex = 0; SU.SetupDiEnumDeviceInfo(hDevInfo, memberIndex,
                info); memberIndex++) {
                HKEY key = SU.SetupDiOpenDevRegKey(hDevInfo, info, SetupApi.DICS_FLAG_GLOBAL, 0,
                    SetupApi.DIREG_DEV, WinNT.KEY_QUERY_VALUE);

                byte[] edid = new byte[1];

                IntByReference pType = new IntByReference();
                IntByReference lpcbData = new IntByReference();

                if (ADV.RegQueryValueEx(key, "EDID", 0, pType, edid, lpcbData)
                    == WinError.ERROR_MORE_DATA) {
                    edid = new byte[lpcbData.getValue()];
                    if (ADV.RegQueryValueEx(key, "EDID", 0, pType, edid, lpcbData)
                        == WinError.ERROR_SUCCESS) {
                        Display display = new WindowsDisplay(edid);
                        displays.add(display);
                    }
                }
                Advapi32.INSTANCE.RegCloseKey(key);
            }
            SU.SetupDiDestroyDeviceInfoList(hDevInfo);
        }
        return displays;
    }
}
