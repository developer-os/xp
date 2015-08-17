package com.enonic.xp.data;

final class StringValueType
    extends ValueType<String>
{
    StringValueType()
    {
        super( "String", JavaTypeConverters.STRING );
    }

    @Override
    Value fromJsonValue( final Object object )
    {
        return ValueFactory.newString( convertNullSafe( object ) );
    }
}
