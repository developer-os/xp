package com.enonic.xp.cache;

public interface Cache<K, V>
{
    void cache( final K key, final V value );

    void evict( final K key );

    void evictAll();

    V get( final K path );
}
