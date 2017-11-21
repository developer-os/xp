package com.enonic.xp.cache;

public interface CacheProvider
{
    Cache getOrCreate( final String name, final CacheConfig config );
}
