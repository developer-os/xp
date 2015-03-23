package com.enonic.xp.form.inputtype;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.form.BreaksRequiredContractException;

public class DateTime
    extends InputType
{

    DateTime()
    {
        super( DateTimeConfig.class );
    }

    @Override
    public AbstractInputTypeConfigJsonSerializer getInputTypeConfigJsonSerializer()
    {
        return DateTimeConfigJsonSerializer.DEFAULT;
    }

    @Override
    public AbstractInputTypeConfigXmlSerializer getInputTypeConfigXmlSerializer()
    {
        return DateTimeConfigXmlSerializer.DEFAULT;
    }

    @Override
    public void checkBreaksRequiredContract( final Property property )
        throws BreaksRequiredContractException
    {

    }

    @Override
    public Value newValue( final String value )
    {

        return Value.newInstant( ValueTypes.DATE_TIME.convert( value ) );
    }

    @Override
    public InputTypeConfig getDefaultConfig()
    {
        return null;
    }
}