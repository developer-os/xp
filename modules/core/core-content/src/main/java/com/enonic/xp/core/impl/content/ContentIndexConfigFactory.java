package com.enonic.xp.core.impl.content;

import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexConfigDocument;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.content.GetContentTypeParams;

import static com.enonic.xp.content.ContentPropertyNames.APPLICATION_KEY;
import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT;
import static com.enonic.xp.content.ContentPropertyNames.ATTACHMENT_TEXT_COMPONENT;
import static com.enonic.xp.content.ContentPropertyNames.CREATED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.CREATOR;
import static com.enonic.xp.content.ContentPropertyNames.DATA;
import static com.enonic.xp.content.ContentPropertyNames.EXTRA_DATA;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIED_TIME;
import static com.enonic.xp.content.ContentPropertyNames.MODIFIER;
import static com.enonic.xp.content.ContentPropertyNames.OWNER;
import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.content.ContentPropertyNames.PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN;
import static com.enonic.xp.content.ContentPropertyNames.SITE;
import static com.enonic.xp.content.ContentPropertyNames.SITECONFIG;
import static com.enonic.xp.content.ContentPropertyNames.TYPE;

public class ContentIndexConfigFactory
{
    private final ContentTypeService contentTypeService;

    public ContentIndexConfigFactory( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    public IndexConfigDocument create( final Node node )
    {
        final PatternIndexConfigDocument.Builder configDocumentBuilder = PatternIndexConfigDocument.create().
            analyzer( ContentConstants.DOCUMENT_INDEX_DEFAULT_ANALYZER ).
            add( CREATOR, IndexConfig.MINIMAL ).
            add( MODIFIER, IndexConfig.MINIMAL ).
            add( CREATED_TIME, IndexConfig.MINIMAL ).
            add( MODIFIED_TIME, IndexConfig.MINIMAL ).
            add( OWNER, IndexConfig.MINIMAL ).
            add( PAGE, IndexConfig.NONE ).
            add( PropertyPath.from( DATA, SITECONFIG, APPLICATION_KEY ), IndexConfig.MINIMAL ).
            add( PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN, IndexConfig.FULLTEXT ).
            add( PropertyPath.from( PAGE, "regions" ), IndexConfig.NONE ).
            add( SITE, IndexConfig.NONE ).
            add( DATA, IndexConfig.BY_TYPE ).
            add( TYPE, IndexConfig.MINIMAL ).
            add( ATTACHMENT, IndexConfig.MINIMAL ).
            add( PropertyPath.from( EXTRA_DATA ), IndexConfig.MINIMAL ).
            defaultConfig( IndexConfig.BY_TYPE );

        final String contentTypeNameString = node.data().getString( ContentPropertyNames.TYPE );
        final ContentTypeName contentTypeName = ContentTypeName.from( contentTypeNameString );

        addAttachmentTextMapping( contentTypeName, configDocumentBuilder );

        final Form form = getForm( contentTypeName );

        final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( DATA, configDocumentBuilder );
        indexConfigVisitor.traverse( form );

        return configDocumentBuilder.build();
    }

    private void addAttachmentTextMapping( final ContentTypeName contentTypeName,
                                           final PatternIndexConfigDocument.Builder configDocumentBuilder )
    {
        if ( contentTypeName.isTextualMedia() )
        {
            configDocumentBuilder.add( ATTACHMENT_TEXT_COMPONENT, IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                includeInAllText( true ).
                nGram( true ).
                decideByType( false ).
                build() );
        }
        else
        {
            configDocumentBuilder.add( ATTACHMENT_TEXT_COMPONENT, IndexConfig.create().
                enabled( true ).
                fulltext( true ).
                includeInAllText( false ).
                nGram( true ).
                decideByType( false ).
                build() );
        }
    }

    private Form getForm( final ContentTypeName contentTypeName )
    {
        return this.contentTypeService.getByName( new GetContentTypeParams().contentTypeName( contentTypeName ) ).getForm();
    }

}
