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
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.ignite.impl.config.IgniteConfig;

@Component(immediate = true, configurationPid = "com.enonic.xp.ignite")
public class IgniteActivator
{
    private ServiceRegistration<Ignite> igniteReg;

    private Ignite ignite;

    private IgniteConfig config;

    private Logger LOG = LoggerFactory.getLogger( IgniteActivator.class );

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        LOG.info( "Activate ignite" );

        final IgniteConfiguration igniteCfg = new IgniteConfiguration();
        igniteCfg.setMetricsLogFrequency( 0 );
        igniteCfg.setDiscoverySpi( DiscoveryFactory.create( this.config ) );

        this.ignite = Ignition.start( igniteCfg );

        this.igniteReg = context.registerService( Ignite.class, ignite, new Hashtable<>() );
    }

    @Deactivate
    public void deactivate()
    {
        LOG.info( "Deactivate ignite" );
        this.igniteReg.unregister();
        this.ignite.close();
    }

    @Reference
    public void setConfig( final IgniteConfig config )
    {
        this.config = config;
    }
}
