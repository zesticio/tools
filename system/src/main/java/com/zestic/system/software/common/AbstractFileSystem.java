
package com.zestic.system.software.common;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.os.FileSystem;
import com.zestic.system.software.os.OSFileStore;
import com.zestic.system.util.GlobalConfig;

import java.util.Arrays;
import java.util.List;

/*
 * Common methods for filesystem implementations
 */
@ThreadSafe public abstract class AbstractFileSystem implements FileSystem {

    public static final String OSHI_NETWORK_FILESYSTEM_TYPES =
        "com.zestic.system.network.filesystem.types";
    public static final String OSHI_PSEUDO_FILESYSTEM_TYPES =
        "com.zestic.system.pseudo.filesystem.types";

    /*
     * FileSystem types which are network-based and should be excluded from
     * local-only lists
     */
    protected static final List<String> NETWORK_FS_TYPES =
        Arrays.asList(GlobalConfig.get(OSHI_NETWORK_FILESYSTEM_TYPES, "").split(","));

    protected static final List<String> PSEUDO_FS_TYPES =
        Arrays.asList(GlobalConfig.get(OSHI_PSEUDO_FILESYSTEM_TYPES, "").split(","));

    @Override public List<OSFileStore> getFileStores() {
        return getFileStores(false);
    }
}
