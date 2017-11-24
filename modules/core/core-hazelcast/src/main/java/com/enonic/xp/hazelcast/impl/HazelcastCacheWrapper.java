package com.enonic.xp.hazelcast.impl;

import com.hazelcast.cache.ICache;

import com.enonic.xp.cache.Cache;

public class HazelcastCacheWrapper<K, V>
    implements Cache<K, V>
{
    private ICache<K, V> cache;

    public HazelcastCacheWrapper( final ICache<K, V> cache )
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
        this.cache.remove( key );
    }

    @Override
    public void evictAll()
    {
        this.cache.removeAll();
    }

    @Override
    public V get( final K path )
    {
        return this.cache.get( path );
    }
}
