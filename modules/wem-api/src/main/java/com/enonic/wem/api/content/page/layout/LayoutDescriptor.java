package com.enonic.wem.api.content.page.layout;

import com.enonic.wem.api.content.page.BaseDescriptor;

public class LayoutDescriptor
    extends BaseDescriptor<LayoutDescriptorKey>
{
    private LayoutDescriptor( final Builder builder )
    {
        super( builder );
    }

    public static LayoutDescriptor.Builder newLayoutDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, LayoutDescriptorKey>
    {
        private Builder()
        {
        }

        public LayoutDescriptor build()
        {
            return new LayoutDescriptor( this );
        }
    }
}
