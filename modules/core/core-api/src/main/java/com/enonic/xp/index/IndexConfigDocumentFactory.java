package com.enonic.xp.index;

import com.enonic.xp.node.Node;

public interface IndexConfigDocumentFactory
{
    boolean supports( final Node node );

    IndexConfigDocument create( final Node node );
}
