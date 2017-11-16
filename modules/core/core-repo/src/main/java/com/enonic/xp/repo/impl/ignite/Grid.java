package com.enonic.xp.repo.impl.ignite;

public interface Grid
{
    Object get( final String key );

    void put( final String key, final Object value );
}
