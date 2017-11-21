package com.enonic.xp.ignite.impl;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.cache.Cache;
import com.enonic.xp.cache.CacheConfig;
import com.enonic.xp.cache.CacheProvider;

@Component(immediate = true)
public class IgniteCacheProvider
    implements CacheProvider
{
    private Ignite ignite;

    @Override
    public Cache getOrCreate( final String name, final CacheConfig config )
    {
        final IgniteCache<Object, Object> existingCache = this.ignite.cache( name );

        if ( existingCache == null )
        {
            System.out.println( "Cache with name: " + name + " not found, create" );
        }

        final IgniteCache<Object, Object> cache = this.ignite.getOrCreateCache( name );

        System.out.println( "Cache '" + name + "' size: " + cache.size() );

        return new IgniteCacheWrapper( cache );
    }

    @Reference
    public void setIgnite( final Ignite ignite )
    {
        this.ignite = ignite;
    }
}
