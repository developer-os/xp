package com.enonic.wem.core.content.schema.content;

import javax.jcr.Session;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.schema.content.GetContentTypes;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.ContentTypes;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.api.content.schema.mixin.MixinFetcher;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.schema.content.dao.ContentTypeDao;
import com.enonic.wem.core.content.schema.mixin.InternalMixinFetcher;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;

@Component
public final class GetContentTypesHandler
    extends CommandHandler<GetContentTypes>
{
    private ContentTypeDao contentTypeDao;

    private MixinDao mixinDao;

    public GetContentTypesHandler()
    {
        super( GetContentTypes.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentTypes command )
        throws Exception
    {
        final Session session = context.getJcrSession();
        final ContentTypes contentTypes;
        if ( command.isGetAll() )
        {
            contentTypes = getAllContentTypes( session );
        }
        else
        {
            final QualifiedContentTypeNames contentTypeNames = command.getNames();
            contentTypes = getContentTypes( session, contentTypeNames );
        }

        if ( command.isMixinReferencesToFormItems() )
        {
            final MixinFetcher mixinFetcher = new InternalMixinFetcher( mixinDao, session );
            for ( ContentType contentType : contentTypes )
            {
                contentType.form().mixinReferencesToFormItems( mixinFetcher );
            }
        }

        command.setResult( contentTypes );
    }

    private ContentTypes getAllContentTypes( final Session session )
    {
        return contentTypeDao.selectAll( session );
    }

    private ContentTypes getContentTypes( final Session session, final QualifiedContentTypeNames contentTypeNames )
    {
        return contentTypeDao.select( contentTypeNames, session );
    }

    @Inject
    public void setContentTypeDao( final ContentTypeDao contentTypeDao )
    {
        this.contentTypeDao = contentTypeDao;
    }

    @Inject
    public void setMixinDao( final MixinDao mixinDao )
    {
        this.mixinDao = mixinDao;
    }
}
