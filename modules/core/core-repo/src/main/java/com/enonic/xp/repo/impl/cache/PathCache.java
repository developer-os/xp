package com.enonic.xp.repo.impl.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.xp.repo.impl.branch.storage.BranchDocumentId;

public class PathCache
    implements com.enonic.xp.cache.Cache<CachePath, BranchDocumentId>
{

    private final Cache<CachePath, BranchDocumentId> pathCache;

    public PathCache()
    {
        pathCache = CacheBuilder.newBuilder().
            maximumSize( 100000 ).
            build();
    }

    @Override
    public void cache( final CachePath key, final BranchDocumentId value )
    {
        this.pathCache.put( key, value );
    }

    @Override
    public void evict( final CachePath key )
    {
        this.pathCache.invalidate( key );
    }

    @Override
    public BranchDocumentId get( final CachePath path )
    {
        return this.pathCache.getIfPresent( path );
    }

    @Override
    public void evictAll()
    {
        this.pathCache.invalidateAll();
    }


}
