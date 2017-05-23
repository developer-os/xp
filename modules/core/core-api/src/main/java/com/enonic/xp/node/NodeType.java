package com.enonic.xp.node;

import com.enonic.xp.index.IndexConfigDocument;

public interface NodeType
{
    IndexConfigDocument createIndexConfigDoc( final Node node );

    NodeTypeName getNodeTypeName();

}
