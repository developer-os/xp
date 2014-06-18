package com.enonic.wem.core.content.site;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentConstants;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.content.UpdateContentParams;
import com.enonic.wem.api.content.editor.ContentEditor;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteNotFoundException;
import com.enonic.wem.api.content.site.UpdateSiteParams;
import com.enonic.wem.api.context.Context;

import static com.enonic.wem.api.content.Content.editContent;

final class UpdateSiteCommand
{
    private UpdateSiteParams params;

    private ContentService contentService;

    public Content execute()
    {
        final Context context = ContentConstants.DEFAULT_CONTEXT;

        final Content content = this.contentService.getById( this.params.getContent(), context );

        if ( content == null )
        {
            throw new ContentNotFoundException( this.params.getContent(), ContentConstants.DEFAULT_CONTEXT.getWorkspace() );
        }
        if ( content.getSite() == null )
        {
            throw new SiteNotFoundException( this.params.getContent() );
        }

        Site.SiteEditBuilder editBuilder = this.params.getEditor().edit( content.getSite() );

        if ( editBuilder.isChanges() )
        {
            final Site editedSite = editBuilder.build();

            final UpdateContentParams updateContent = new UpdateContentParams().
                contentId( this.params.getContent() ).
                editor( new ContentEditor()
                {
                    @Override
                    public Content.EditBuilder edit( final Content toBeEdited )
                    {
                        return editContent( toBeEdited ).site( editedSite );
                    }
                } );

            this.contentService.update( updateContent, context );
        }

        return this.contentService.getById( this.params.getContent(), context );
    }

    public UpdateSiteCommand params( final UpdateSiteParams params )
    {
        this.params = params;
        return this;
    }

    public UpdateSiteCommand contentService( final ContentService contentService )
    {
        this.contentService = contentService;
        return this;
    }
}
