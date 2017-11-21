package com.enonic.xp.repo.impl.node;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.repo.impl.Grid;

public class TestGrid
    implements Grid
{
    private Map<String, Object> values = Maps.newHashMap();

    @Override
    public Object get( final String key )
    {
        return values.get( key );
    }

    @Override
    public void put( final String key, final Object value )
    {
        this.values.put( key, value );
    }
}
