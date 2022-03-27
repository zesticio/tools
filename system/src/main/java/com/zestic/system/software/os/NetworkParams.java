
package com.zestic.system.software.os;

import com.zestic.system.annotation.concurrent.ThreadSafe;

/*
 * NetworkParams presents network parameters of running OS, such as DNS, host
 * name etc.
 */
@ThreadSafe public interface NetworkParams {

    /*
     * Gets the HostName of the machine executing com.zestic.system.
     *
     * @return the hostname
     */
    String getHostName();

    /*
     * Gets the Domain Name of the machine executing com.zestic.system.
     *
     * @return the domain name
     */
    String getDomainName();

    /*
     * Gets the DNS Servers configured for this machine.
     *
     * @return the DNS servers
     */
    String[] getDnsServers();

    /*
     * Gets the default gateway(routing destination for 0.0.0.0/0) for IPv4
     * connections.
     *
     * @return default gateway for IPv4, or empty string if not defined.
     */
    String getIpv4DefaultGateway();

    /*
     * Gets default gateway(routing destination for ::/0) for IPv6 connections.
     *
     * @return default gateway for IPv6, or empty string if not defined.
     */
    String getIpv6DefaultGateway();
}
