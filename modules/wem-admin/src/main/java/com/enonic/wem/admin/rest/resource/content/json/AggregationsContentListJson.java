package com.enonic.wem.admin.rest.resource.content.json;

import com.google.common.collect.ImmutableSet;

import com.enonic.wem.admin.json.content.ContentJson;
import com.enonic.wem.admin.rest.resource.content.ContentIconUrlResolver;
import com.enonic.wem.api.aggregation.Aggregations;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentListMetaData;
import com.enonic.wem.api.content.Contents;

public class AggregationsContentListJson
    extends AbstractAggregationContentListJson<ContentJson>
{
    public AggregationsContentListJson( final Content content, final ContentListMetaData contentListMetaData,
                                        final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver )
    {
        super( content, contentListMetaData, aggregations, iconUrlResolver );
    }

    public AggregationsContentListJson( final Contents contents, final ContentListMetaData contentListMetaData,
                                        final Aggregations aggregations, final ContentIconUrlResolver iconUrlResolver )
    {
        super( contents, contentListMetaData, ImmutableSet.copyOf( aggregations ), iconUrlResolver );
    }

    @Override
    protected ContentJson createItem( final Content content )
    {
        return new ContentJson( content, iconUrlResolver );
    }
}
