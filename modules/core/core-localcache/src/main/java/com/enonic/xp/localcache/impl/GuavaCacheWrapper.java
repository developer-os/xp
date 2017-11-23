package com.enonic.xp.localcache.impl;


import com.enonic.xp.cache.Cache;

public class GuavaCacheWrapper<K, V>
    implements Cache<K, V>
{
    private final com.google.common.cache.Cache<K, V> cache;

    public GuavaCacheWrapper( final com.google.common.cache.Cache<K, V> cache )
    {
        this.cache = cache;
    }

    @Override
    public void cache( final K key, final V value )
    {
        this.cache.put( key, value );
    }

    @Override
    public void evict( final K key )
    {
        this.cache.invalidate( key );
    }

    @Override
    public void evictAll()
    {
        this.cache.invalidateAll();
    }

    @Override
    public V get( final K path )
    {
        return this.cache.getIfPresent( path );
    }
}