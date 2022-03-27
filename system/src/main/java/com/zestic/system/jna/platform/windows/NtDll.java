
package com.zestic.system.jna.platform.windows;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.W32APIOptions;

public interface NtDll extends com.sun.jna.platform.win32.NtDll {

    NtDll INSTANCE = Native.load("NtDll", NtDll.class, W32APIOptions.DEFAULT_OPTIONS);

    int PROCESS_BASIC_INFORMATION = 0;

    /*
     * Windows API docs say NtQueryInformationProcess may be altered or unavailable
     * in future versions of Windows. Applications should use the alternate
     * functions listed in this topic. However, there is no other way to get this
     * information, it's been officially non-API for over a decade, and many many
     * programs including windows sysinternal tools rely on this behavior, so the
     * odds of it going away are small.
     */
    int NtQueryInformationProcess(HANDLE ProcessHandle, int ProcessInformationClass,
        Pointer ProcessInformation, int ProcessInformationLength, IntByReference ReturnLength);


    @FieldOrder({"Reserved1", "PebBaseAddress", "Reserved2"}) class PROCESS_BASIC_INFORMATION
        extends Structure {
        public Pointer Reserved1;
        public Pointer PebBaseAddress;
        public Pointer[] Reserved2 = new Pointer[4];
    }


    @FieldOrder({"pad", "pad2", "ProcessParameters"}) class PEB extends Structure {
        public byte[] pad = new byte[4];
        public Pointer[] pad2 = new Pointer[3];
        public Pointer ProcessParameters; // RTL_USER_PROCESS_PARAMETERS
    }


    @FieldOrder({"MaximumLength", "Length", "Flags", "DebugFlags", "ConsoleHandle", "ConsoleFlags",
        "StandardInput", "StandardOutput", "StandardError", "CurrentDirectory", "DllPath",
        "ImagePathName", "CommandLine", "Environment", "StartingX", "StartingY", "CountX", "CountY",
        "CountCharsX", "CountCharsY", "FillAttribute", "WindowFlags", "ShowWindowFlags",
        "WindowTitle", "DesktopInfo", "ShellInfo", "RuntimeData", "CurrentDirectories",
        "EnvironmentSize", "EnvironmentVersion", "PackageDependencyData", "ProcessGroupId",
        "LoaderThreads", "RedirectionDllName", "HeapPartitionName", "DefaultThreadpoolCpuSetMasks",
        "DefaultThreadpoolCpuSetMaskCount"}) class RTL_USER_PROCESS_PARAMETERS extends Structure {
        public int MaximumLength;
        public int Length;
        public int Flags;
        public int DebugFlags;
        public HANDLE ConsoleHandle;
        public int ConsoleFlags;
        public HANDLE StandardInput;
        public HANDLE StandardOutput;
        public HANDLE StandardError;
        public CURDIR CurrentDirectory;
        public UNICODE_STRING DllPath;
        public UNICODE_STRING ImagePathName;
        public UNICODE_STRING CommandLine;
        public Pointer Environment;
        public int StartingX;
        public int StartingY;
        public int CountX;
        public int CountY;
        public int CountCharsX;
        public int CountCharsY;
        public int FillAttribute;
        public int WindowFlags;
        public int ShowWindowFlags;
        public UNICODE_STRING WindowTitle;
        public UNICODE_STRING DesktopInfo;
        public UNICODE_STRING ShellInfo;
        public UNICODE_STRING RuntimeData;
        public RTL_DRIVE_LETTER_CURDIR[] CurrentDirectories = new RTL_DRIVE_LETTER_CURDIR[32];
        public ULONG_PTR EnvironmentSize;
        public ULONG_PTR EnvironmentVersion;
        public Pointer PackageDependencyData;
        public int ProcessGroupId;
        public int LoaderThreads;
        public UNICODE_STRING RedirectionDllName;
        public UNICODE_STRING HeapPartitionName;
        public ULONG_PTR DefaultThreadpoolCpuSetMasks;
        public int DefaultThreadpoolCpuSetMaskCount;
    }


    @FieldOrder({"Length", "MaximumLength", "Buffer"}) class UNICODE_STRING extends Structure {
        public short Length;
        public short MaximumLength;
        public Pointer Buffer;
    }


    @FieldOrder({"Length", "MaximumLength", "Buffer"}) class STRING extends Structure {
        public short Length;
        public short MaximumLength;
        public Pointer Buffer;
    }


    @FieldOrder({"DosPath", "Handle"}) class CURDIR extends Structure {
        public UNICODE_STRING DosPath;
        public Pointer Handle;
    }


    @FieldOrder({"Flags", "Length", "TimeStamp", "DosPath"}) class RTL_DRIVE_LETTER_CURDIR
        extends Structure {
        public short Flags;
        public short Length;
        public int TimeStamp;
        public STRING DosPath;
    }
}
