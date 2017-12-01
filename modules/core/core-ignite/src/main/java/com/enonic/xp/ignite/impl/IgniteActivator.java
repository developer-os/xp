package com.enonic.xp.ignite.impl;

import java.util.Hashtable;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.osgi.classloaders.ContainerSweepClassLoader;
import org.jetbrains.annotations.NotNull;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.ignite.impl.config.IgniteConfig;

@Component(immediate = true)
public class IgniteActivator
{
    private ServiceRegistration<Ignite> igniteReg;

    private Ignite ignite;

    private IgniteConfig config;

    private final static Logger LOG = LoggerFactory.getLogger( IgniteActivator.class );

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        final IgniteConfiguration igniteCfg = new IgniteConfiguration();
        igniteCfg.setDiscoverySpi( DiscoveryFactory.create( this.config ) );
        igniteCfg.setClassLoader( createClassLoader( context ) );

        this.ignite = Ignition.start( igniteCfg );

        this.igniteReg = context.registerService( Ignite.class, ignite, new Hashtable<>() );
    }

    @NotNull
    private ContainerSweepClassLoader createClassLoader( final BundleContext context )
    {
        return new ContainerSweepClassLoader( context.getBundle(), this.getClass().getClassLoader() );
    }

    @Reference
    public void setConfig( final IgniteConfig config )
    {
        this.config = config;
    }
}
