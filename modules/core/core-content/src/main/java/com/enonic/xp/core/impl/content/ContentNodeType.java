package com.enonic.xp.core.impl.content;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeTypeName;
import com.enonic.xp.schema.content.ContentTypeService;

@Component(immediate = true, service = NodeType.class)
@Order(-100)
public class ContentNodeType
    implements NodeType
{
    private ContentTypeService contentTypeService;

    private ContentIndexConfigFactory indexConfigFactory;

    @SuppressWarnings("unused")
    @Activate
    public void initialize()
    {
        this.indexConfigFactory = new ContentIndexConfigFactory( this.contentTypeService );
    }

    @Override
    public IndexConfigDocument createIndexConfigDoc( final Node node )
    {
        return this.indexConfigFactory.create( node );
    }

    @Override
    public NodeTypeName getNodeTypeName()
    {
        return NodeTypeName.from( "content" );
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }
}
