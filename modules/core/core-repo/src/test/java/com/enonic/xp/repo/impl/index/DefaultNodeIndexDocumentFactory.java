package com.enonic.xp.repo.impl.index;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.IndexConfigDocumentFactory;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.repo.impl.node.NodeConstants;

@Component
public class DefaultNodeIndexDocumentFactory
    implements IndexConfigDocumentFactory
{
    @Override
    public boolean supports( final Node node )
    {
        return true;
    }

    @Override
    public IndexConfigDocument create( final Node node )
    {
        return PatternIndexConfigDocument.create().
            analyzer( NodeConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
            defaultConfig( IndexConfig.BY_TYPE ).
            build();
    }
}
