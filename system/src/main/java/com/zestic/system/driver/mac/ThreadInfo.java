
package com.zestic.system.driver.mac;

import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.os.OSProcess;
import com.zestic.system.util.ExecutingCommand;
import com.zestic.system.util.ParseUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * Utility to query threads for a process
 */
@ThreadSafe public final class ThreadInfo {

    private static final Pattern PS_M = Pattern.compile(
        "\\D+(\\d+).+(\\d+\\.\\d)\\s+(\\w)\\s+(\\d+)\\D+(\\d+:\\d{2}\\.\\d{2})\\s+(\\d+:\\d{2}\\.\\d{2}).+");

    private ThreadInfo() {
    }

    public static List<ThreadStats> queryTaskThreads(int pid) {
        String pidStr = " " + pid + " ";
        List<ThreadStats> taskThreads = new ArrayList<>();
        // Only way to get thread info without root permissions
        // Using the M switch gives all threads with no possibility to filter
        List<String> psThread =
            ExecutingCommand.runNative("ps -awwxM").stream().filter(s -> s.contains(pidStr))
                .collect(Collectors.toList());
        int tid = 0;
        for (String thread : psThread) {
            Matcher m = PS_M.matcher(thread);
            if (m.matches() && pid == ParseUtil.parseIntOrDefault(m.group(1), -1)) {
                double cpu = ParseUtil.parseDoubleOrDefault(m.group(2), 0d);
                char state = m.group(3).charAt(0);
                int pri = ParseUtil.parseIntOrDefault(m.group(4), 0);
                long sTime = ParseUtil.parseDHMSOrDefault(m.group(5), 0L);
                long uTime = ParseUtil.parseDHMSOrDefault(m.group(6), 0L);
                taskThreads.add(new ThreadStats(tid++, cpu, state, sTime, uTime, pri));
            }
        }
        return taskThreads;
    }

    /*
     * Class to encapsulate mach thread info
     */
    @Immutable public static class ThreadStats {
        private final int threadId;
        private final long userTime;
        private final long systemTime;
        private final long upTime;
        private final OSProcess.State state;
        private final int priority;

        public ThreadStats(int tid, double cpu, char state, long sTime, long uTime, int pri) {
            this.threadId = tid;
            this.userTime = uTime;
            this.systemTime = sTime;
            // user + system / uptime = cpu/100
            // so: uptime = user+system / cpu/100
            this.upTime = (long) ((uTime + sTime) / (cpu / 100d + 0.0005));
            switch (state) {
                case 'I':
                case 'S':
                    this.state = OSProcess.State.SLEEPING;
                    break;
                case 'U':
                    this.state = OSProcess.State.WAITING;
                    break;
                case 'R':
                    this.state = OSProcess.State.RUNNING;
                    break;
                case 'Z':
                    this.state = OSProcess.State.ZOMBIE;
                    break;
                case 'T':
                    this.state = OSProcess.State.STOPPED;
                    break;
                default:
                    this.state = OSProcess.State.OTHER;
                    break;
            }
            this.priority = pri;
        }

        /*
         * @return the threadId
         */
        public int getThreadId() {
            return threadId;
        }

        /*
         * @return the userTime
         */
        public long getUserTime() {
            return userTime;
        }

        /*
         * @return the systemTime
         */
        public long getSystemTime() {
            return systemTime;
        }

        /*
         * @return the upTime
         */
        public long getUpTime() {
            return upTime;
        }

        /*
         * @return the state
         */
        public OSProcess.State getState() {
            return state;
        }

        /*
         * @return the priority
         */
        public int getPriority() {
            return priority;
        }
    }
}
