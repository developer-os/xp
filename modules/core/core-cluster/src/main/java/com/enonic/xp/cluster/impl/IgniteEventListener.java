package com.enonic.xp.cluster.impl;

import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.lang.IgnitePredicate;

public class IgniteEventListener
    implements IgnitePredicate<DiscoveryEvent>
{
    @Override
    public boolean apply( final DiscoveryEvent discoveryEvent )
    {

        System.out.println( "*********** Received DiscoveryEvent: " + discoveryEvent );

        return true;
    }
}
