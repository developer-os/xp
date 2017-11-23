package com.enonic.xp.repo.impl.node;

import java.util.Map;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import com.enonic.xp.cache.Cache;
import com.enonic.xp.cache.CacheConfig;
import com.enonic.xp.cache.CacheProvider;

public class TestCacheProvider
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
        return caches.computeIfAbsent( name, k -> new TestCacheWrapper( CacheBuilder.newBuilder().
            maximumSize( 1000 ).
            build() ) );
    }
}
