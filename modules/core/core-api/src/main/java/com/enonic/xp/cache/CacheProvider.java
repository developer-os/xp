package com.enonic.xp.cache;

public interface CacheProvider
{
    <K, V> Cache<K, V> get( final String name );

    <K, V> Cache<K, V> getOrCreate( final String name, final CacheConfig config );
}
