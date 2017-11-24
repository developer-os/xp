package com.enonic.xp.ignite.impl.config;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Lists;

import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.xp.ignite")
public class IgniteConfigImpl
    implements IgniteConfig
{
    private Configuration config;

    @Activate
    public void activate( final Map<String, String> map )
    {
        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }

    @Override
    public List<String> unicastHosts()
    {
        final String hosts = this.config.get( "discovery.unicast.hosts" );
        return Lists.newArrayList( hosts.split( "," ) );
    }
}
