
package com.zestic.system.software.os.windows;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinBase.SYSTEM_INFO;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;


/*
 * Windows OS native system information.
 */
public class WindowsOSSystemInfo {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);

    // Populated during call to init
    private SYSTEM_INFO systemInfo = null;

    /*
     * Constructor for WindowsOSSystemInfo.
     */
    public WindowsOSSystemInfo() {
        init();
    }

    /*
     * Constructor for WindowsOSSystemInfo.
     *
     * @param si a {@link com.sun.jna.platform.win32.WinBase.SYSTEM_INFO} object.
     */
    public WindowsOSSystemInfo(SYSTEM_INFO si) {
        this.systemInfo = si;
    }

    private void init() {
        SYSTEM_INFO si = new SYSTEM_INFO();
        Kernel32.INSTANCE.GetSystemInfo(si);

        try {
            IntByReference isWow64 = new IntByReference();
            // This returns a pseudo handle, currently (HANDLE)-1, that is
            // interpreted as the current process handle. The pseudo handle need
            // not be closed when it is no longer needed. Calling the
            // CloseHandle function with a pseudo handle has no effect.
            HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
            if (Kernel32.INSTANCE.IsWow64Process(hProcess, isWow64) && isWow64.getValue() > 0) {
                // Populates the class variable with information
                Kernel32.INSTANCE.GetNativeSystemInfo(si);
            }
        } catch (UnsatisfiedLinkError e) {
            // no WOW64 support
            LOG.trace("No WOW64 support: {}" + e.getMessage());
        }

        this.systemInfo = si;
        LOG.debug("Initialized OSNativeSystemInfo");
    }

    /*
     * Number of processors.
     *
     * @return Integer.
     */
    public int getNumberOfProcessors() {
        return this.systemInfo.dwNumberOfProcessors.intValue();
    }
}
