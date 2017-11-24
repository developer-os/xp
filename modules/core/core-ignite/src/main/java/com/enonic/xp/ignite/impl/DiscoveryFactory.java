package com.enonic.xp.ignite.impl;

import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.jetbrains.annotations.NotNull;

import com.enonic.xp.ignite.impl.config.IgniteConfig;

public class DiscoveryFactory
{

    public static DiscoverySpi create( final IgniteConfig config )
    {
        final TcpDiscoverySpi discoverySpi = new TcpDiscoverySpi();
        final TcpDiscoveryVmIpFinder staticIpFinder = createStaticIpFinder( config );
        discoverySpi.setIpFinder( staticIpFinder );

        return discoverySpi;
    }

    @NotNull
    private static TcpDiscoveryVmIpFinder createStaticIpFinder( final IgniteConfig config )
    {
        final TcpDiscoveryVmIpFinder staticIpFinder = new TcpDiscoveryVmIpFinder();
        staticIpFinder.setAddresses( config.unicastHosts() );
        return staticIpFinder;
    }

}
