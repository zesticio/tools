
package com.zestic.system.hardware.common;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.hardware.CentralProcessor;
import com.zestic.system.util.Memoizer;
import com.zestic.system.util.ParseUtil;

import java.util.*;
import java.util.function.Supplier;

/*
 * A CPU.
 */
@ThreadSafe
public abstract class AbstractCentralProcessor implements CentralProcessor {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AbstractCentralProcessor.class);

    private final Supplier<ProcessorIdentifier> cpuid = Memoizer.memoize(this::queryProcessorId);
    private final Supplier<Long> maxFreq =
            Memoizer.memoize(this::queryMaxFreq, Memoizer.defaultExpiration());
    private final Supplier<long[]> currentFreq =
            Memoizer.memoize(this::queryCurrentFreq, Memoizer.defaultExpiration());
    private final Supplier<Long> contextSwitches =
            Memoizer.memoize(this::queryContextSwitches, Memoizer.defaultExpiration());
    private final Supplier<Long> interrupts =
            Memoizer.memoize(this::queryInterrupts, Memoizer.defaultExpiration());

    private final Supplier<long[]> systemCpuLoadTicks =
            Memoizer.memoize(this::querySystemCpuLoadTicks, Memoizer.defaultExpiration());
    private final Supplier<long[][]> processorCpuLoadTicks =
            Memoizer.memoize(this::queryProcessorCpuLoadTicks, Memoizer.defaultExpiration());

    // Logical and Physical Processor Counts
    private final int physicalPackageCount;
    private final int physicalProcessorCount;
    private final int logicalProcessorCount;

    // Processor info, initialized in constructor
    private final List<LogicalProcessor> logicalProcessors;

    /*
     * Create a Processor
     */
    protected AbstractCentralProcessor() {
        // Populate logical processor array.
        this.logicalProcessors = Collections.unmodifiableList(initProcessorCounts());
        // Init processor counts
        Set<String> physProcPkgs = new HashSet<>();
        Set<Integer> physPkgs = new HashSet<>();
        for (LogicalProcessor logProc : this.logicalProcessors) {
            int pkg = logProc.getPhysicalPackageNumber();
            physProcPkgs.add(logProc.getPhysicalProcessorNumber() + ":" + pkg);
            physPkgs.add(pkg);
        }
        this.logicalProcessorCount = this.logicalProcessors.size();
        this.physicalProcessorCount = physProcPkgs.size();
        this.physicalPackageCount = physPkgs.size();
    }

    /*
     * Creates a Processor ID by encoding the stepping, model, family, and feature
     * flags.
     *
     * @param stepping The CPU stepping
     * @param model    The CPU model
     * @param family   The CPU family
     * @param flags    A space-delimited list of CPU feature flags
     * @return The Processor ID string
     */
    protected static String createProcessorID(String stepping, String model, String family,
                                              String[] flags) {
        long processorIdBytes = 0L;
        long steppingL = ParseUtil.parseLongOrDefault(stepping, 0L);
        long modelL = ParseUtil.parseLongOrDefault(model, 0L);
        long familyL = ParseUtil.parseLongOrDefault(family, 0L);
        // 3:0 – Stepping
        processorIdBytes |= steppingL & 0xf;
        // 19:16,7:4 – Model
        processorIdBytes |= (modelL & 0x0f) << 4;
        processorIdBytes |= (modelL & 0xf0) << 16;
        // 27:20,11:8 – Family
        processorIdBytes |= (familyL & 0x0f) << 8;
        processorIdBytes |= (familyL & 0xf0) << 20;
        // 13:12 – Processor Type, assume 0
        for (String flag : flags) {
            switch (flag) { // NOSONAR squid:S1479
                case "fpu":
                    processorIdBytes |= 1L << 32;
                    break;
                case "vme":
                    processorIdBytes |= 1L << 33;
                    break;
                case "de":
                    processorIdBytes |= 1L << 34;
                    break;
                case "pse":
                    processorIdBytes |= 1L << 35;
                    break;
                case "tsc":
                    processorIdBytes |= 1L << 36;
                    break;
                case "msr":
                    processorIdBytes |= 1L << 37;
                    break;
                case "pae":
                    processorIdBytes |= 1L << 38;
                    break;
                case "mce":
                    processorIdBytes |= 1L << 39;
                    break;
                case "cx8":
                    processorIdBytes |= 1L << 40;
                    break;
                case "apic":
                    processorIdBytes |= 1L << 41;
                    break;
                case "sep":
                    processorIdBytes |= 1L << 43;
                    break;
                case "mtrr":
                    processorIdBytes |= 1L << 44;
                    break;
                case "pge":
                    processorIdBytes |= 1L << 45;
                    break;
                case "mca":
                    processorIdBytes |= 1L << 46;
                    break;
                case "cmov":
                    processorIdBytes |= 1L << 47;
                    break;
                case "pat":
                    processorIdBytes |= 1L << 48;
                    break;
                case "pse-36":
                    processorIdBytes |= 1L << 49;
                    break;
                case "psn":
                    processorIdBytes |= 1L << 50;
                    break;
                case "clfsh":
                    processorIdBytes |= 1L << 51;
                    break;
                case "ds":
                    processorIdBytes |= 1L << 53;
                    break;
                case "acpi":
                    processorIdBytes |= 1L << 54;
                    break;
                case "mmx":
                    processorIdBytes |= 1L << 55;
                    break;
                case "fxsr":
                    processorIdBytes |= 1L << 56;
                    break;
                case "sse":
                    processorIdBytes |= 1L << 57;
                    break;
                case "sse2":
                    processorIdBytes |= 1L << 58;
                    break;
                case "ss":
                    processorIdBytes |= 1L << 59;
                    break;
                case "htt":
                    processorIdBytes |= 1L << 60;
                    break;
                case "tm":
                    processorIdBytes |= 1L << 61;
                    break;
                case "ia64":
                    processorIdBytes |= 1L << 62;
                    break;
                case "pbe":
                    processorIdBytes |= 1L << 63;
                    break;
                default:
                    break;
            }
        }
        return String.format("%016X", processorIdBytes);
    }

    /*
     * Updates logical and physical processor counts and arrays
     *
     * @return An array of initialized Logical Processors
     */
    protected abstract List<LogicalProcessor> initProcessorCounts();

    /*
     * Updates logical and physical processor counts and arrays
     *
     * @return An array of initialized Logical Processors
     */
    protected abstract ProcessorIdentifier queryProcessorId();

    @Override
    public ProcessorIdentifier getProcessorIdentifier() {
        return cpuid.get();
    }

    @Override
    public long getMaxFreq() {
        return maxFreq.get();
    }

    /*
     * Get processor max frequency.
     *
     * @return The max frequency.
     */
    protected abstract long queryMaxFreq();

    @Override
    public long[] getCurrentFreq() {
        long[] freq = currentFreq.get();
        if (freq.length == getLogicalProcessorCount()) {
            return freq;
        }
        long[] freqs = new long[getLogicalProcessorCount()];
        Arrays.fill(freqs, freq[0]);
        return freqs;
    }

    /*
     * Get processor current frequency.
     *
     * @return The current frequency.
     */
    protected abstract long[] queryCurrentFreq();

    @Override
    public long getContextSwitches() {
        return contextSwitches.get();
    }

    /*
     * Get number of context switches
     *
     * @return The context switches
     */
    protected abstract long queryContextSwitches();

    @Override
    public long getInterrupts() {
        return interrupts.get();
    }

    /*
     * Get number of interrupts
     *
     * @return The interrupts
     */
    protected abstract long queryInterrupts();

    @Override
    public List<LogicalProcessor> getLogicalProcessors() {
        return this.logicalProcessors;
    }

    @Override
    public long[] getSystemCpuLoadTicks() {
        return systemCpuLoadTicks.get();
    }

    /*
     * Get the system CPU load ticks
     *
     * @return The system CPU load ticks
     */
    protected abstract long[] querySystemCpuLoadTicks();

    @Override
    public long[][] getProcessorCpuLoadTicks() {
        return processorCpuLoadTicks.get();
    }

    /*
     * Get the processor CPU load ticks
     *
     * @return The processor CPU load ticks
     */
    protected abstract long[][] queryProcessorCpuLoadTicks();

    @Override
    public double getSystemCpuLoadBetweenTicks(long[] oldTicks) {
        if (oldTicks.length != TickType.values().length) {
            throw new IllegalArgumentException(
                    "Tick array " + oldTicks.length + " should have " + TickType.values().length
                            + " elements");
        }
        long[] ticks = getSystemCpuLoadTicks();
        // Calculate total
        long total = 0;
        for (int i = 0; i < ticks.length; i++) {
            total += ticks[i] - oldTicks[i];
        }
        // Calculate idle from difference in idle and IOwait
        long idle = ticks[TickType.IDLE.getIndex()] + ticks[TickType.IOWAIT.getIndex()]
                - oldTicks[TickType.IDLE.getIndex()] - oldTicks[TickType.IOWAIT.getIndex()];
        logger.trace("Total ticks: {}  Idle ticks: {}" + total + " " + idle);

        return total > 0 && idle >= 0 ? (double) (total - idle) / total : 0d;
    }

    @Override
    public double[] getProcessorCpuLoadBetweenTicks(long[][] oldTicks) {
        if (oldTicks.length != this.logicalProcessorCount
                || oldTicks[0].length != TickType.values().length) {
            throw new IllegalArgumentException(
                    "Tick array " + oldTicks.length + " should have " + this.logicalProcessorCount
                            + " arrays, each of which has " + TickType.values().length + " elements");
        }
        long[][] ticks = getProcessorCpuLoadTicks();
        double[] load = new double[this.logicalProcessorCount];
        for (int cpu = 0; cpu < this.logicalProcessorCount; cpu++) {
            long total = 0;
            for (int i = 0; i < ticks[cpu].length; i++) {
                total += ticks[cpu][i] - oldTicks[cpu][i];
            }
            // Calculate idle from difference in idle and IOwait
            long idle =
                    ticks[cpu][TickType.IDLE.getIndex()] + ticks[cpu][TickType.IOWAIT.getIndex()]
                            - oldTicks[cpu][TickType.IDLE.getIndex()]
                            - oldTicks[cpu][TickType.IOWAIT.getIndex()];
            logger.trace("CPU: {}  Total ticks: {}  Idle ticks: {}" + cpu + " " + total + " " + idle);
            // update
            load[cpu] = total > 0 && idle >= 0 ? (double) (total - idle) / total : 0d;
        }
        return load;
    }

    @Override
    public int getLogicalProcessorCount() {
        return this.logicalProcessorCount;
    }

    @Override
    public int getPhysicalProcessorCount() {
        return this.physicalProcessorCount;
    }

    @Override
    public int getPhysicalPackageCount() {
        return this.physicalPackageCount;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getProcessorIdentifier().getName());
        sb.append("\n ").append(getPhysicalPackageCount()).append(" physical CPU package(s)");
        sb.append("\n ").append(getPhysicalProcessorCount()).append(" physical CPU core(s)");
        sb.append("\n ").append(getLogicalProcessorCount()).append(" logical CPU(s)");
        sb.append('\n').append("Identifier: ").append(getProcessorIdentifier().getIdentifier());
        sb.append('\n').append("ProcessorID: ").append(getProcessorIdentifier().getProcessorID());
        sb.append('\n').append("Microarchitecture: ")
                .append(getProcessorIdentifier().getMicroarchitecture());
        return sb.toString();
    }
}
