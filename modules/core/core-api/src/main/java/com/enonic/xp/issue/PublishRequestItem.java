package com.enonic.xp.issue;

import java.util.Objects;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.repository.RepositoryConstants;

public class PublishRequestItem
{
    private final ContentId id;

    private Boolean includeChildren;

    public ContentId getId()
    {
        return id;
    }

    public Boolean getIncludeChildren()
    {
        return includeChildren;
    }

    private PublishRequestItem( Builder builder )
    {
        this.id = builder.id;
        this.includeChildren = builder.includeChildren;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final PublishRequestItem that = (PublishRequestItem) o;

        return Objects.equals( id, that.id ) && this.includeChildren == includeChildren;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( id, includeChildren );
    }

    public static class Builder
    {
        private ContentId id;

        private Boolean includeChildren = false;

        public Builder()
        {
        }

        public Builder id( final ContentId id )
        {
            this.id = id;
            return this;
        }

        public Builder includeChildren( final Boolean includeChildren)
        {
            this.includeChildren = includeChildren;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( id, "content id cannot be null" );
        }

        public PublishRequestItem build()
        {
            validate();
            return new PublishRequestItem( this );
        }
    }
}
