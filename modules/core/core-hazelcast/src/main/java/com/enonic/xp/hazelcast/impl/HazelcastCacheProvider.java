package com.enonic.xp.hazelcast.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.cache.ICache;
import com.hazelcast.core.HazelcastInstance;

import com.enonic.xp.cache.Cache;
import com.enonic.xp.cache.CacheConfig;

@Component(immediate = true)
public class HazelcastCacheProvider
    //implements CacheProvider
{
    private HazelcastInstance instance;

    // @Override
    public <K, V> Cache<K, V> get( final String name )
    {
        return doGetCache( name );
    }

    private <K, V> Cache<K, V> doGetCache( final String name )
    {
        final ICache<K, V> cache = this.instance.getCacheManager().getCache( name );
        return new HazelcastCacheWrapper<>( cache );
    }

    //@Override
    public <K, V> Cache<K, V> getOrCreate( final String name, final CacheConfig config )
    {
        return doGetCache( name );
    }

    @Reference
    public void setInstance( final HazelcastInstance instance )
    {
        this.instance = instance;
        System.out.println( "Hazelcast instance: " + instance.getName() );
        System.out.println( "Hazelcast instance: " + instance.getCacheManager().getCache( "myCache" ) );
    }
}
