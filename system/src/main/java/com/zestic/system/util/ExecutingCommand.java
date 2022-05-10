
package com.zestic.system.util;

import com.zestic.system.PlatformEnum;
import com.zestic.system.SystemInfo;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.platform.unix.aix.AixNetworkIF;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * A class for executing on the command line and returning the result of
 * execution.
 */
@ThreadSafe
public final class ExecutingCommand {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(AixNetworkIF.class);

    private static final String[] DEFAULT_ENV = getDefaultEnv();

    private ExecutingCommand() {
    }

    private static String[] getDefaultEnv() {
        PlatformEnum platform = SystemInfo.getCurrentPlatform();
        if (platform == PlatformEnum.WINDOWS) {
            return new String[]{"LANGUAGE=C"};
        } else if (platform != PlatformEnum.UNKNOWN) {
            return new String[]{"LC_ALL=C"};
        } else {
            return null; // NOSONAR squid:S1168
        }
    }

    /*
     * Executes a command on the native command line and returns the result. This is
     * a convenience method to call {@link Runtime#exec(String)} and
     * capture the resulting output in a list of Strings. On Windows, built-in
     * commands not associated with an executable program may require
     * {@code cmd.exe /c} to be prepended to the command.
     *
     * @param cmdToRun Command to run
     * @return A list of Strings representing the result of the command, or empty
     * string if the command failed
     */
    public static List<String> runNative(String cmdToRun) {
        String[] cmd = cmdToRun.split(" ");
        return runNative(cmd);
    }

    /*
     * Executes a command on the native command line and returns the result line by
     * line. This is a convenience method to call
     * {@link Runtime#exec(String[])} and capture the resulting output in
     * a list of Strings. On Windows, built-in commands not associated with an
     * executable program may require the strings {@code cmd.exe} and {@code /c} to
     * be prepended to the array.
     *
     * @param cmdToRunWithArgs Command to run and args, in an array
     * @return A list of Strings representing the result of the command, or empty
     * string if the command failed
     */
    public static List<String> runNative(String[] cmdToRunWithArgs) {
        return runNative(cmdToRunWithArgs, DEFAULT_ENV);
    }

    /*
     * Executes a command on the native command line and returns the result line by
     * line. This is a convenience method to call
     * {@link Runtime#exec(String[])} and capture the resulting output in
     * a list of Strings. On Windows, built-in commands not associated with an
     * executable program may require the strings {@code cmd.exe} and {@code /c} to
     * be prepended to the array.
     *
     * @param cmdToRunWithArgs Command to run and args, in an array
     * @param envp             array of strings, each element of which has environment variable
     *                         settings in the format name=value, or null if the subprocess
     *                         should inherit the environment of the current process.
     * @return A list of Strings representing the result of the command, or empty
     * string if the command failed
     */
    public static List<String> runNative(String[] cmdToRunWithArgs, String[] envp) {
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(cmdToRunWithArgs, envp);
        } catch (SecurityException | IOException e) {
            LOG.trace("Couldn't run command {" + Arrays.toString(cmdToRunWithArgs) + "}: {" + e.getMessage() + "}");
            return new ArrayList<>(0);
        }

        ArrayList<String> sa = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream(), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sa.add(line);
            }
            p.waitFor();
        } catch (IOException e) {
            LOG.trace("Problem reading output from {"+Arrays.toString(cmdToRunWithArgs)+"}: {"+e.getMessage()+"}");
            return new ArrayList<>(0);
        } catch (InterruptedException ie) {
            LOG.trace("Interrupted while reading output from {"+Arrays.toString(cmdToRunWithArgs)+"}: {"+ie.getMessage()+"}");
            Thread.currentThread().interrupt();
        }
        return sa;
    }

    /*
     * Return first line of response for selected command.
     *
     * @param cmd2launch String command to be launched
     * @return String or empty string if command failed
     */
    public static String getFirstAnswer(String cmd2launch) {
        return getAnswerAt(cmd2launch, 0);
    }

    /*
     * Return response on selected line index (0-based) after running selected
     * command.
     *
     * @param cmd2launch String command to be launched
     * @param answerIdx  int index of line in response of the command
     * @return String whole line in response or empty string if invalid index or
     * running of command fails
     */
    public static String getAnswerAt(String cmd2launch, int answerIdx) {
        List<String> sa = ExecutingCommand.runNative(cmd2launch);

        if (answerIdx >= 0 && answerIdx < sa.size()) {
            return sa.get(answerIdx);
        }
        return "";
    }

}
