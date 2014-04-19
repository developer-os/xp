package com.enonic.wem.api.content.page.part;

import javax.xml.bind.annotation.XmlRootElement;

import com.enonic.wem.api.content.page.DescriptorKey;
import com.enonic.wem.api.content.page.PageComponentXml;
import com.enonic.wem.api.xml.XmlObject;

@XmlRootElement(name = "part-component")
public final class PartComponentXml
    extends PageComponentXml
    implements XmlObject<PartComponent, PartComponent.Builder>
{

    @Override
    public void from( final PartComponent component )
    {
        super.from( component );
    }

    @Override
    public void to( final PartComponent.Builder builder )
    {
        super.to( builder );
    }

    @Override
    protected DescriptorKey toDescriptorKey( final String s )
    {
        return PartDescriptorKey.from( s );
    }
}
