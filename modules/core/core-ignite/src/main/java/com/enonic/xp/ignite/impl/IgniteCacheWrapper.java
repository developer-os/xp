package com.enonic.xp.ignite.impl;

import org.apache.ignite.IgniteCache;

import com.enonic.xp.cache.Cache;

public class IgniteCacheWrapper<K, V>
    implements Cache<K, V>
{
    private final IgniteCache<K, V> cache;

    public IgniteCacheWrapper( final IgniteCache cache )
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
    public V get( final K key )
    {
        final V v = this.cache.get( key );

        if ( v != null )
        {
            System.out.println( "Found in cache: " + key.toString() );
        }
        else
        {
            System.out.println( " ----> Not in cache: " + key.toString() );
        }

        return v;
    }
}
