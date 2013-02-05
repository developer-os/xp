package com.enonic.wem.api.exception;

import com.enonic.wem.api.content.relationshiptype.QualifiedRelationshipTypeName;

public final class RelationshipTypeNotFoundException
    extends BaseException
{
    public RelationshipTypeNotFoundException( final QualifiedRelationshipTypeName qName )
    {
        super( "RelationshipType [{0}] was not found", qName );
    }
}
