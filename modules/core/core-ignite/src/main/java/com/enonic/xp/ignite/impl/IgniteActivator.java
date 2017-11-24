package com.enonic.xp.ignite.impl;

import java.util.Hashtable;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.ignite.impl.config.IgniteConfig;

@Component(immediate = true)
public class IgniteActivator
{
    private ServiceRegistration<Ignite> igniteReg;

    private IgniteConfig config;

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        final IgniteConfiguration igniteCfg = new IgniteConfiguration();
        igniteCfg.setDiscoverySpi( DiscoveryFactory.create( this.config ) );

        //final Ignite ignite = Ignition.start();
        Ignite ignite = Ignition.start( igniteCfg );

        System.out.println( " -----------------------------------------------------------------" );
        System.out.println( "------Setting ignite object (ACTIVATOR): " + ignite.hashCode() );
        System.out.println( " -----------------------------------------------------------------" );

        this.igniteReg = context.registerService( Ignite.class, ignite, new Hashtable<>() );
    }

    @Reference
    public void setConfig( final IgniteConfig config )
    {
        this.config = config;
    }
}
