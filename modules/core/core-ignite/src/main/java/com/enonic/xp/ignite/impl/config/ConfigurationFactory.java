package com.enonic.xp.ignite.impl.config;

import java.nio.file.Paths;

import org.apache.ignite.configuration.IgniteConfiguration;

import com.google.common.base.Strings;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.home.HomeDir;

public class ConfigurationFactory
{
    private final ClusterConfig clusterConfig;

    private final IgniteSettings igniteConfig;

    private ConfigurationFactory( final Builder builder )
    {
        clusterConfig = builder.clusterConfig;
        igniteConfig = builder.igniteConfig;
    }

    public IgniteConfiguration execute()
    {
        final IgniteConfiguration config = new IgniteConfiguration();

        config.setIgniteInstanceName( InstanceNameResolver.resolve() );
        config.setConsistentId( clusterConfig.name().toString() );
        config.setIgniteHome( resolveIgniteHome() );

        if ( !Strings.isNullOrEmpty( igniteConfig.localhost() ) )
        {
            config.setLocalHost( igniteConfig.localhost() );
        }

        config.setDiscoverySpi( DiscoveryFactory.create().
            discovery( clusterConfig.discovery() ).
            igniteConfig( igniteConfig ).
            build().
            execute() );

        config.setGridLogger( LoggerConfig.create() );

        config.setMetricsLogFrequency( igniteConfig.metrics_log_frequency() );

        return config;
    }

    private String resolveIgniteHome()
    {
        return Paths.get( HomeDir.get().toFile().getPath(), igniteConfig.home().split( "/" ) ).toString();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private ClusterConfig clusterConfig;

        private IgniteSettings igniteConfig;

        private Builder()
        {
        }

        public Builder clusterConfig( final ClusterConfig val )
        {
            clusterConfig = val;
            return this;
        }

        public Builder igniteConfig( final IgniteSettings val )
        {
            igniteConfig = val;
            return this;
        }

        public ConfigurationFactory build()
        {
            return new ConfigurationFactory( this );
        }
    }
}