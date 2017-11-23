package com.enonic.xp.cache;

public interface CacheProvider
{
    Cache get( final String name );

    Cache getOrCreate( final String name, final CacheConfig config );
}
