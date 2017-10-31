package com.enonic.xp.core.impl.content.index.processor;

import java.util.Arrays;

import com.enonic.xp.core.impl.content.index.IndexConfigVisitor;
import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.form.Form;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexValueProcessors;
import com.enonic.xp.index.PatternIndexConfigDocument;

import static com.enonic.xp.content.ContentPropertyNames.PAGE;
import static com.enonic.xp.data.PropertyPath.ELEMENT_DIVIDER;

public class PageConfigProcessor
    implements ContentIndexConfigProcessor
{
    private final Form pageConfigForm;

    public static final String CONFIG = "config";

    public static final String PAGE_CONFIG = String.join( ELEMENT_DIVIDER,"page", "config");

    public static final String LAYOUT_COMPONENT = "page.region.**.LayoutComponent";

    public static final String PART_COMPONENT = "page.region.**.PartComponent";

    public static final String TEXT_COMPONENT = "page.region.**.TextComponent";

    public static final String PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN = String.join( ELEMENT_DIVIDER, TEXT_COMPONENT, "text");

    public static final String ALL_PATTERN = "*";

    public final static IndexConfig TEXT_COMPONENT_INDEX_CONFIG = IndexConfig.create( IndexConfig.FULLTEXT ).
        addIndexValueProcessor( IndexValueProcessors.HTML_STRIPPER ).
        build();

    public PageConfigProcessor( final Form pageConfigForm )
    {
        this.pageConfigForm = pageConfigForm;
    }

    @Override
    public PatternIndexConfigDocument.Builder processDocument( final PatternIndexConfigDocument.Builder builder )
    {

        builder.add( PAGE, IndexConfig.NONE ).
            add( PropertyPath.from( PAGE, "controller" ), IndexConfig.MINIMAL ).
            add( PropertyPath.from( PAGE_CONFIG, ALL_PATTERN ), IndexConfig.BY_TYPE ).
            add( PAGE_TEXT_COMPONENT_PROPERTY_PATH_PATTERN, TEXT_COMPONENT_INDEX_CONFIG ).
            add( PropertyPath.from( PAGE, "regions" ), IndexConfig.NONE );

        if ( this.pageConfigForm != null && this.pageConfigForm.getFormItems().size() > 0 )
        {
            final IndexConfigVisitor indexConfigVisitor = new IndexConfigVisitor( PAGE_CONFIG, builder );
            indexConfigVisitor.traverse( pageConfigForm );
        }

        processRegions( builder );

        return builder;
    }

    private PatternIndexConfigDocument.Builder processRegions( final PatternIndexConfigDocument.Builder builder )
    {
        builder.add( String.join( ELEMENT_DIVIDER, LAYOUT_COMPONENT, CONFIG, ALL_PATTERN), IndexConfig.BY_TYPE );
        builder.add( String.join( ELEMENT_DIVIDER, PART_COMPONENT, CONFIG, ALL_PATTERN), IndexConfig.BY_TYPE );
        builder.add( String.join( ELEMENT_DIVIDER, TEXT_COMPONENT, CONFIG, ALL_PATTERN), IndexConfig.BY_TYPE );

        builder.add( String.join( ELEMENT_DIVIDER, TEXT_COMPONENT, ALL_PATTERN), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, PART_COMPONENT, ALL_PATTERN), IndexConfig.MINIMAL );
        builder.add( String.join( ELEMENT_DIVIDER, LAYOUT_COMPONENT, ALL_PATTERN), IndexConfig.MINIMAL );

//        builder.add( String.join( ELEMENT_DIVIDER, UNIVERSAL_COMPONENT, CONFIG, ALL_PATTERN), IndexConfig.BY_TYPE );
//        builder.add( String.join( ELEMENT_DIVIDER, UNIVERSAL_COMPONENT, ALL_PATTERN), IndexConfig.MINIMAL );

        return builder;
    }
}
