package com.enonic.wem.api.content.page.part;


import com.enonic.wem.api.content.page.BaseDescriptor;

public class PartDescriptor
    extends BaseDescriptor<PartDescriptorKey>
{
    private PartDescriptor( final Builder builder )
    {
        super( builder );
    }

    public static PartDescriptor.Builder newPartDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder, PartDescriptorKey>
    {
        private Builder()
        {
        }

        public PartDescriptor build()
        {
            return new PartDescriptor( this );
        }
    }
}
