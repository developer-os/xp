package com.enonic.wem.core.exporters;

import java.io.IOException;
import java.nio.file.Path;

import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.xml.XmlSerializers;
import com.enonic.wem.xml.template.PartTemplateXml;

@XMLFilename("part-template.xml")
public final class PartTemplateExporter
    extends AbstractEntityExporter<PartTemplate, PartTemplate.Builder>
{
    @Override
    protected String toXMLString( final PartTemplate partTemplate )
    {
        final PartTemplateXml partTemplateXml = new PartTemplateXml();
        partTemplateXml.from( partTemplate );
        return XmlSerializers.partTemplate().serialize( partTemplateXml );
    }

    @Override
    protected PartTemplate.Builder fromXMLString( final String xml, final Path directoryPath )
        throws IOException
    {
        final PartTemplate.Builder builder = PartTemplate.newPartTemplate();
        XmlSerializers.partTemplate().parse( xml ).to( builder );
        return builder;
    }
}
