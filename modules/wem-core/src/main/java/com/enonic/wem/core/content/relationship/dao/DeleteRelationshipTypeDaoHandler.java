package com.enonic.wem.core.content.relationship.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.relationship.QualifiedRelationshipTypeName;
import com.enonic.wem.api.exception.SystemException;

final class DeleteRelationshipTypeDaoHandler
    extends AbstractRelationshipTypeDaoHandler

{
    DeleteRelationshipTypeDaoHandler( final Session session )
    {
        super( session );
    }

    void handle( final QualifiedRelationshipTypeName relationshipTypeName )
        throws RepositoryException
    {
        final Node relationshipTypeNode = getRelationshipTypeNode( relationshipTypeName );

        if ( relationshipTypeNode == null )
        {
            throw new SystemException( "Relationship type [{0}] was not found", relationshipTypeName );
        }

        relationshipTypeNode.remove();
    }
}
