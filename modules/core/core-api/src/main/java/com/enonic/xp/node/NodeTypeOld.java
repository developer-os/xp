package com.enonic.xp.node;

import com.google.common.annotations.Beta;

import com.enonic.xp.index.IndexConfigDocumentFactory;

@Beta
public final class NodeTypeOld
{
    public final static NodeTypeOld DEFAULT_NODE_COLLECTION = NodeTypeOld.from( "default" );

    private final NodeTypeName name;

    private IndexConfigDocumentFactory indexConfigDocFactory;

    private NodeTypeOld( final String name )
    {
        this.name = NodeTypeName.from( name );
    }

    public static NodeTypeOld from( final String name )
    {
        return new NodeTypeOld( name );
    }

    public String getName()
    {
        return name.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final NodeTypeOld that = (NodeTypeOld) o;

        if ( name != null ? !name.equals( that.name ) : that.name != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString()
    {
        return this.name.toString();
    }
}
