
package com.zestic.system.driver.unix.solaris.kstat;

import com.sun.jna.platform.unix.solaris.LibKstat.Kstat;
import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.platform.unix.solaris.KstatUtil;
import com.zestic.system.util.tuples.Pair;

/*
 * Utility to query geom part list
 */
@ThreadSafe public final class SystemPages {

    private SystemPages() {
    }

    /*
     * Queries the {@code system_pages} kstat and returns available and physical
     * memory
     *
     * @return A pair with the available and total memory, in pages. Mutiply by page
     * size for bytes.
     */
    public static Pair<Long, Long> queryAvailableTotal() {
        long memAvailable = 0;
        long memTotal = 0;
        // Get first result
        try (KstatUtil.KstatChain kc = KstatUtil.openChain()) {
            Kstat ksp = KstatUtil.KstatChain.lookup(null, -1, "system_pages");
            // Set values
            if (ksp != null && KstatUtil.KstatChain.read(ksp)) {
                memAvailable = KstatUtil.dataLookupLong(ksp, "availrmem"); // not a typo
                memTotal = KstatUtil.dataLookupLong(ksp, "physmem");
            }
        }
        return new Pair<>(memAvailable, memTotal);
    }
}
