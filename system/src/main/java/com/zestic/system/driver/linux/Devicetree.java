
package com.zestic.system.driver.linux;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.util.FileUtil;

/*
 * Utility to read info from the devicetree
 */
@ThreadSafe public final class Devicetree {

    private Devicetree() {
    }

    /*
     * Query the model from the devicetree
     *
     * @return The model if available, null otherwise
     */
    public static String queryModel() {
        String modelStr = FileUtil.getStringFromFile("/sys/firmware/devicetree/base/model");
        if (!modelStr.isEmpty()) {
            return modelStr.replace("Machine: ", "");
        }
        return null;
    }
}
