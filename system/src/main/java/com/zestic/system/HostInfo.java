package com.zestic.system;

import com.zestic.core.net.NetUtil;

import java.io.Serializable;
import java.net.InetAddress;

public class HostInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String HOST_NAME;
    private final String HOST_ADDRESS;

    public HostInfo() {
        final InetAddress localhost = NetUtil.getLocalhost();
        if (null != localhost) {
            HOST_NAME = localhost.getHostName();
            HOST_ADDRESS = localhost.getHostAddress();
        } else {
            HOST_NAME = null;
            HOST_ADDRESS = null;
        }
    }

    public final String getName() {
        return HOST_NAME;
    }

    public final String getAddress() {
        return HOST_ADDRESS;
    }

    @Override public final String toString() {
        StringBuilder builder = new StringBuilder();

        SystemUtil.append(builder, "Host Name:    ", getName());
        SystemUtil.append(builder, "Host Address: ", getAddress());

        return builder.toString();
    }

}
