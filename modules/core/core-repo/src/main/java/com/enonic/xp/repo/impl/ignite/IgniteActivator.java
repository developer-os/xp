package com.enonic.xp.repo.impl.ignite;

import java.util.Hashtable;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.CacheConfiguration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, configurationPid = "com.enonic.xp.ignite")
public class IgniteActivator
{
    private Ignite ignite;

    private ServiceRegistration<Ignite> igniteReg;

    @Activate
    public void activate( final BundleContext context, final Map<String, String> map )
    {
        this.ignite = Ignition.start();
        this.igniteReg = context.registerService( Ignite.class, this.ignite, new Hashtable<>() );

        CacheConfiguration<String, Object> cfg = new CacheConfiguration<>( "pathCache" );
        cfg.setBackups( 1 );

        final IgniteCache<String, Object> IngniteCache = ignite.getOrCreateCache( cfg );


    }


}
