package com.enonic.wem.core.content;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.api.command.content.GetContentVersion;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentSelector;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.core.command.CommandContext;
import com.enonic.wem.core.command.CommandHandler;
import com.enonic.wem.core.content.dao.ContentDao;

@Component
public class GetContentVersionHandler
    extends CommandHandler<GetContentVersion>
{
    private ContentDao contentDao;

    public GetContentVersionHandler()
    {
        super( GetContentVersion.class );
    }

    @Override
    public void handle( final CommandContext context, final GetContentVersion command )
        throws Exception
    {
        final ContentSelector selector = command.getSelector();
        final ContentVersionId versionId = command.getVersion();
        final Content content = contentDao.getContentVersion( selector, versionId, context.getJcrSession() );

        command.setResult( content );
    }

    @Inject
    public void setContentDao( final ContentDao contentDao )
    {
        this.contentDao = contentDao;
    }
}
