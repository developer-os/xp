package com.enonic.wem.api.content.page;

public class LayoutDescriptor
    extends BaseDescriptor
{
    private LayoutDescriptor( final Builder builder )
    {
        super( builder.name, builder.displayName, builder.controllerResource, builder.config );
    }

    public static LayoutDescriptor.Builder newLayoutDescriptor()
    {
        return new Builder();
    }

    public static class Builder
        extends BaseDescriptorBuilder<Builder>
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
