package com.enonic.xp.localcache.impl;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import com.enonic.xp.cache.Cache;
import com.enonic.xp.cache.CacheConfig;
import com.enonic.xp.cache.CacheProvider;

@Component(immediate = true)
public class LocalCacheProvider
    implements CacheProvider
{
    private Map<String, Cache> caches = Maps.newHashMap();

    @Override
    public Cache get( final String name )
    {
        return this.caches.get( name );
    }

    @Override
    public Cache getOrCreate( final String name, final CacheConfig config )
    {
        return caches.computeIfAbsent( name, k -> new GuavaCacheWrapper( CacheBuilder.newBuilder().
            maximumSize( 100000 ).
            build() ) );
    }
}
