
package com.zestic.system.util.platform.windows;

import com.sun.jna.platform.win32.BaseTSD.DWORD_PTR;
import com.sun.jna.platform.win32.*;
import com.sun.jna.platform.win32.Pdh.PDH_RAW_COUNTER;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONGLONGByReference;
import com.sun.jna.platform.win32.WinNT.HANDLEByReference;
import com.zestic.system.annotation.concurrent.Immutable;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.FormatUtil;
import com.zestic.system.util.ParseUtil;
import com.zestic.system.util.Util;

/*
 * Helper class to centralize the boilerplate portions of PDH counter setup and
 * allow applications to easily add, query, and remove counters.
 */
@ThreadSafe
public final class PerfDataUtil {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(PerfCounterQuery.class);

    private static final DWORD_PTR PZERO = new DWORD_PTR(0);
    private static final DWORDByReference PDH_FMT_RAW =
            new DWORDByReference(new DWORD(Pdh.PDH_FMT_RAW));
    private static final Pdh PDH = Pdh.INSTANCE;

    private static final boolean IS_VISTA_OR_GREATER = VersionHelpers.IsWindowsVistaOrGreater();


    private PerfDataUtil() {
    }

    /*
     * Create a Performance Counter
     *
     * @param object   The object/path for the counter
     * @param instance The instance of the counter, or null if no instance
     * @param counter  The counter name
     * @return A PerfCounter object encapsulating the object, instance, and counter
     */
    public static PerfCounter createCounter(String object, String instance, String counter) {
        return new PerfCounter(object, instance, counter);
    }

    /*
     * Update a query and get the timestamp
     *
     * @param query The query to update all counters in
     * @return The update timestamp of the first counter in the query
     */
    public static long updateQueryTimestamp(WinNT.HANDLEByReference query) {
        LONGLONGByReference pllTimeStamp = new LONGLONGByReference();
        int ret = IS_VISTA_OR_GREATER ?
                PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) :
                PDH.PdhCollectQueryData(query.getValue());
        // Due to race condition, initial update may fail with PDH_NO_DATA.
        int retries = 0;
        while (ret == PdhMsg.PDH_NO_DATA && retries++ < 3) {
            // Exponential fallback.
            Util.sleep(1 << retries);
            ret = IS_VISTA_OR_GREATER ?
                    PDH.PdhCollectQueryDataWithTime(query.getValue(), pllTimeStamp) :
                    PDH.PdhCollectQueryData(query.getValue());
        }
        if (ret != WinError.ERROR_SUCCESS) {
            LOG.warn("Failed to update counter. Error code: {}" +
                    String.format(FormatUtil.formatError(ret)));
            return 0L;
        }
        // Perf Counter timestamp is in local time
        return IS_VISTA_OR_GREATER ?
                ParseUtil.filetimeToUtcMs(pllTimeStamp.getValue().longValue(), true) :
                System.currentTimeMillis();
    }

    /*
     * Open a pdh query
     *
     * @param q pointer to the query
     * @return true if successful
     */
    public static boolean openQuery(HANDLEByReference q) {
        int ret = PDH.PdhOpenQuery(null, PZERO, q);
        if (ret != WinError.ERROR_SUCCESS) {
            LOG.error("Failed to open PDH Query. Error code: {}" +
                    String.format(FormatUtil.formatError(ret)));
            return false;
        }
        return true;
    }

    /*
     * Close a pdh query
     *
     * @param q pointer to the query
     * @return true if successful
     */
    public static boolean closeQuery(HANDLEByReference q) {
        return WinError.ERROR_SUCCESS == PDH.PdhCloseQuery(q.getValue());
    }

    /*
     * Get value of pdh counter
     *
     * @param counter The counter to get the value of
     * @return long value of the counter, or negative value representing an error
     * code
     */
    public static long queryCounter(WinNT.HANDLEByReference counter) {
        PDH_RAW_COUNTER counterValue = new PDH_RAW_COUNTER();
        int ret = PDH.PdhGetRawCounterValue(counter.getValue(), PDH_FMT_RAW, counterValue);
        if (ret != WinError.ERROR_SUCCESS) {
            LOG.warn("Failed to get counter. Error code: {}" +
                    String.format(FormatUtil.formatError(ret)));
            return ret;
        }
        return counterValue.FirstValue;
    }

    /*
     * Adds a pdh counter to a query
     *
     * @param query Pointer to the query to add the counter
     * @param path  String name of the PerfMon counter. For Vista+, must be in
     *              English. Must localize this path for pre-Vista.
     * @param p     Pointer to the counter
     * @return true if successful
     */
    public static boolean addCounter(WinNT.HANDLEByReference query, String path,
                                     WinNT.HANDLEByReference p) {
        int ret = IS_VISTA_OR_GREATER ?
                PDH.PdhAddEnglishCounter(query.getValue(), path, PZERO, p) :
                PDH.PdhAddCounter(query.getValue(), path, PZERO, p);
        if (ret != WinError.ERROR_SUCCESS) {
            LOG.warn("Failed to add PDH Counter: {}, Error code: {}" + path + " " + String.format(FormatUtil.formatError(ret)));
            return false;
        }
        return true;
    }

    /*
     * Remove a pdh counter
     *
     * @param p pointer to the counter
     * @return true if successful
     */
    public static boolean removeCounter(HANDLEByReference p) {
        return WinError.ERROR_SUCCESS == PDH.PdhRemoveCounter(p.getValue());
    }


    /*
     * Encapsulates the three string components of a performance counter
     */
    @Immutable
    public static class PerfCounter {
        private String object;
        private String instance;
        private String counter;

        public PerfCounter(String objectName, String instanceName, String counterName) {
            this.object = objectName;
            this.instance = instanceName;
            this.counter = counterName;
        }

        /*
         * @return Returns the object.
         */
        public String getObject() {
            return object;
        }

        /*
         * @return Returns the instance.
         */
        public String getInstance() {
            return instance;
        }

        /*
         * @return Returns the counter.
         */
        public String getCounter() {
            return counter;
        }

        /*
         * Returns the path for this counter
         *
         * @return A string representing the counter path
         */
        public String getCounterPath() {
            StringBuilder sb = new StringBuilder();
            sb.append('\\').append(object);
            if (instance != null) {
                sb.append('(').append(instance).append(')');
            }
            sb.append('\\').append(counter);
            return sb.toString();
        }
    }
}
