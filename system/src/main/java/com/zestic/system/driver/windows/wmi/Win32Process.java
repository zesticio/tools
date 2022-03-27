
package com.zestic.system.driver.windows.wmi;

import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiQuery;
import com.sun.jna.platform.win32.COM.WbemcliUtil.WmiResult;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.windows.WmiQueryHandler;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/*
 * Utility to query WMI class {@code Win32_Process}
 */
@ThreadSafe public final class Win32Process {

    private static final String WIN32_PROCESS = "Win32_Process";


    private Win32Process() {
    }

    /*
     * Returns process command lines
     *
     * @param pidsToQuery Process IDs to query for command lines. Pass {@code null} to query
     *                    all processes.
     * @return A {@link WmiResult} containing process IDs and command lines used to
     * start the provided processes.
     */
    public static WmiResult<CommandLineProperty> queryCommandLines(Set<Integer> pidsToQuery) {
        String sb = WIN32_PROCESS;
        if (pidsToQuery != null) {
            sb += " WHERE ProcessID=" + pidsToQuery.stream().map(String::valueOf)
                .collect(Collectors.joining(" OR PROCESSID="));
        }
        WmiQuery<CommandLineProperty> commandLineQuery =
            new WmiQuery<>(sb, CommandLineProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(commandLineQuery);
    }

    /*
     * Returns process info
     *
     * @param pids Process IDs to query.
     * @return Information on the provided processes.
     */
    public static WmiResult<ProcessXPProperty> queryProcesses(Collection<Integer> pids) {
        String sb = WIN32_PROCESS;
        if (pids != null) {
            sb += " WHERE ProcessID=" + pids.stream().map(String::valueOf)
                .collect(Collectors.joining(" OR PROCESSID="));
        }
        WmiQuery<ProcessXPProperty> processQueryXP = new WmiQuery<>(sb, ProcessXPProperty.class);
        return Objects.requireNonNull(WmiQueryHandler.createInstance()).queryWMI(processQueryXP);
    }

    /*
     * Process command lines.
     */
    public enum CommandLineProperty {
        PROCESSID, COMMANDLINE;
    }

    /*
     * Process properties accessible from WTSEnumerateProcesses in Vista+
     */
    public enum ProcessXPProperty {
        PROCESSID, NAME, KERNELMODETIME, USERMODETIME, THREADCOUNT, PAGEFILEUSAGE, HANDLECOUNT, EXECUTABLEPATH;
    }
}
