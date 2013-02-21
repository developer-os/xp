package com.enonic.wem.core.content.schema.relationship;

import javax.jcr.Session;

import org.joda.time.DateTime;
import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.content.schema.relationship.RelationshipType;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.relationship.dao.RelationshipTypeDao;

import static com.enonic.wem.api.content.schema.relationship.RelationshipType.newRelationshipType;

@Component
public final class CreateRelationshipTypeHandler
    extends CommandHandler<CreateRelationshipType>
{
    private RelationshipTypeDao relationshipTypeDao;

    public CreateRelationshipTypeHandler()
    {
        super( CreateRelationshipType.class );
    }

    @Override
    public void handle( final CommandContext context, final CreateRelationshipType command )
        throws Exception
    {
        final DateTime currentTime = DateTime.now();

        final RelationshipType.Builder builder = newRelationshipType();
        builder.name( command.getName() );
        builder.displayName( command.getDisplayName() );
        builder.module( command.getModule() );
        builder.fromSemantic( command.getFromSemantic() );
        builder.toSemantic( command.getToSemantic() );
        if ( command.getAllowedFromTypes() != null )
        {
            builder.addAllowedFromTypes( command.getAllowedFromTypes() );
        }
        if ( command.getAllowedToTypes() != null )
        {
            builder.addAllowedToTypes( command.getAllowedToTypes() );
        }
        builder.createdTime( currentTime );
        builder.modifiedTime( currentTime );
        builder.icon( command.getIcon() );
        final RelationshipType relationshipType = builder.build();

        final Session session = context.getJcrSession();
        relationshipTypeDao.create( relationshipType, session );
        session.save();
        command.setResult( relationshipType.getQualifiedName() );
    }

    @Inject
    public void setRelationshipTypeDao( final RelationshipTypeDao relationshipTypeDao )
    {
        this.relationshipTypeDao = relationshipTypeDao;
    }
}
