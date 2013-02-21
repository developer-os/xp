package com.enonic.wem.core.content.schema.relationship;

import javax.jcr.Session;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.relationship.GetRelationshipTypes;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeNames;
import com.enonic.wem.api.content.schema.relationship.RelationshipTypes;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

@Component
public final class GetRelationshipTypesHandler
    extends CommandHandler<GetRelationshipTypes>
{
    private RelationshipTypeDao relationshipTypeDao;

    public GetRelationshipTypesHandler()
    {
        super( GetRelationshipTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final GetRelationshipTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final RelationshipTypes relationshipTypes;
        if ( command.isGetAll() )
        {
            relationshipTypes = relationshipTypeDao.selectAll( session );
        }
        else
        {
            final QualifiedRelationshipTypeNames selectors = command.getQualifiedNames();
            relationshipTypes = relationshipTypeDao.select( selectors, session );
        }
        command.setResult( relationshipTypes );
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }

}
