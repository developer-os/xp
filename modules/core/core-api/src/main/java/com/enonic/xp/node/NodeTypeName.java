package com.enonic.xp.node;

public class NodeTypeName
{
    private String value;

    private NodeTypeName( final String value )
    {
        this.value = value;
    }

    public static NodeTypeName from( final String value )
    {
        return new NodeTypeName( value );
    }


    @Override
    public String toString()
    {
        return this.value;
    }
}
