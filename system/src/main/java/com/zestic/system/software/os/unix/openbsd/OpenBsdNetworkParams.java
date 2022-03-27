
package com.zestic.system.software.os.unix.openbsd;

import com.zestic.system.annotation.concurrent.ThreadSafe;
import com.zestic.system.software.common.AbstractNetworkParams;
import com.zestic.system.util.ExecutingCommand;

/*
 * OpenBsdNetworkParams class.
 */
@ThreadSafe public class OpenBsdNetworkParams extends AbstractNetworkParams {
    @Override public String getIpv4DefaultGateway() {
        return searchGateway(ExecutingCommand.runNative("route -n get default"));
    }

    @Override public String getIpv6DefaultGateway() {
        return searchGateway(ExecutingCommand.runNative("route -n get default"));
    }
}
