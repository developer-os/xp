package com.enonic.xp.core.impl.content;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;

public class UnpublishContentCommand
    extends AbstractContentCommand
{
    private final UnpublishContentParams params;

    private UnpublishContentCommand( final Builder builder )
    {
        super( builder );

        this.params = builder.params;
    }

    public Contents execute()
    {
        final Context context = ContextAccessor.current();

        final Context unpublishContext = ContextBuilder.from( context ).
            branch( params.getUnpublishBranch() ).
            build();

        return unpublishContext.callWith( () -> unpublish() );
    }

    private Contents unpublish()
    {
        Contents.Builder contents = Contents.create();

        for ( final ContentId contentId : this.params.getContentIds() )
        {
            final Node node = this.nodeService.deleteById( NodeId.from( contentId ) );

            if ( node != null )
            {
                contents.add( translator.fromNode( node, false ) );
            }
        }

        return contents.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
        extends AbstractContentCommand.Builder<Builder>
    {
        private UnpublishContentParams params;

        public Builder params( final UnpublishContentParams params )
        {
            this.params = params;
            return this;
        }

        @Override
        void validate()
        {
            Preconditions.checkNotNull( params );
        }

        public UnpublishContentCommand build()
        {
            validate();
            return new UnpublishContentCommand( this );
        }
    }

}

