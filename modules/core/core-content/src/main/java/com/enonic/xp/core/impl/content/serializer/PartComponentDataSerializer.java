package com.enonic.xp.core.impl.content.serializer;


import com.enonic.xp.data.PropertySet;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.region.PartComponent;

public class PartComponentDataSerializer
    extends DescriptorBasedComponentDataSerializer<PartComponent, PartComponent>
{

    @Override
    public void toData( final PartComponent component, final PropertySet parent )
    {
        final PropertySet asData = parent.addSet( COMPONENTS );
        applyComponentToData( component, asData );
    }

    @Override
    public PartComponent fromData( final SerializedData data )
    {
        PartComponent.Builder component = PartComponent.create();
        applyComponentFromData( component, data.getAsData() );
        return component.build();
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return DescriptorKey.from( s );
    }

}
