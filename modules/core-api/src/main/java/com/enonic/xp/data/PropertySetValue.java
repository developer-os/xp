package com.enonic.xp.data;

final class PropertySetValue
    extends Value
{
    public PropertySetValue( final PropertySet value )
    {
        super( ValueTypes.PROPERTY_SET, value );
    }

    public PropertySetValue( final PropertySetValue source, final PropertyTree tree )
    {
        super( ValueTypes.PROPERTY_SET, !source.isNull() ? source.asData().copy( tree ) : null );
    }

    @Override
    public Value copy( final PropertyTree tree )
    {
        return new PropertySetValue( this, tree );
    }
}
