package com.enonic.wem.core.content.schema.mixin;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.mixin.DeleteMixins;
import com.enonic.wem.api.content.schema.mixin.MixinDeletionResult;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.exception.MixinNotFoundException;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;

@Component
public final class DeleteMixinsHandler
    extends CommandHandler<DeleteMixins>
{
    private MixinDao mixinDao;

    //private ContentTypeDao contentTypeDao;

    public DeleteMixinsHandler()
    {
        super( DeleteMixins.class );
    }

    @Override
    public void handle( final CommandContext context, final DeleteMixins command )
        throws Exception
    {
        final MixinDeletionResult mixinDeletionResult = new MixinDeletionResult();

        for ( QualifiedMixinName qualifiedMixinName : command.getNames() )
        {
            try
            {
                /* TODO: if ( contentTypeDao.countMixinUsage( qualifiedMixinName, context.getJcrSession() ) > 0 )
                {
                    Exception e = new UnableToDeleteMixinException( qualifiedMixinName, "Mixin is being used." );
                    mixinDeletionResult.failure( qualifiedMixinName, e );
                }
                else
                {*/
                mixinDao.delete( qualifiedMixinName, context.getJcrSession() );
                mixinDeletionResult.success( qualifiedMixinName );
                context.getJcrSession().save();
                //}
            }
            catch ( MixinNotFoundException e )
            {
                mixinDeletionResult.failure( qualifiedMixinName, e );
            }
        }

        command.setResult( mixinDeletionResult );
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }

    /*@Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }*/
}
