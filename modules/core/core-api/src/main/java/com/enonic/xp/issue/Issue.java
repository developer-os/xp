package com.enonic.xp.issue;

import java.time.Instant;
import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.name.NamePrettyfier;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public class Issue
{
    private final IssueId id;

    private final String title;

    private final IssueName name;

    private final IssuePath issuePath;

    private final String description;

    private final Instant createdTime;

    private final Instant modifiedTime;

    private final IssueStatus issueStatus;

    private final PrincipalKey creator;

    private final PrincipalKeys approverIds;

    private final ContentIds itemIds;

    private Issue( Builder builder )
    {
        this.id = IssueId.create();
        this.title = builder.title;
        this.name = IssueName.from( NamePrettyfier.create( builder.title ) );
        this.issuePath = IssuePath.from( this.name );
        this.description = builder.description;
        this.createdTime = builder.createdTime;
        this.modifiedTime = builder.modifiedTime;
        this.issueStatus = builder.issueStatus;
        this.creator = builder.creator;
        this.approverIds = PrincipalKeys.from( builder.approverIds );
        this.itemIds = ContentIds.from( builder.itemIds );
    }

    public IssueId getId()
    {
        return id;
    }

    public String getTitle()
    {
        return title;
    }

    public IssueName getName()
    {
        return name;
    }

    public IssuePath getPath()
    {
        return issuePath;
    }

    public String getDescription()
    {
        return description;
    }

    public Instant getCreatedTime()
    {
        return createdTime;
    }

    public Instant getModifiedTime()
    {
        return modifiedTime;
    }

    public IssueStatus getStatus()
    {
        return issueStatus;
    }

    public PrincipalKey getCreator()
    {
        return creator;
    }

    public PrincipalKeys getApproverIds()
    {
        return approverIds;
    }

    public ContentIds getItemIds()
    {
        return itemIds;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String title;

        private String description;

        private Instant createdTime;

        private Instant modifiedTime;

        private IssueStatus issueStatus;

        private PrincipalKey creator;

        private List<PrincipalKey> approverIds;

        private List<ContentId> itemIds;

        public Builder()
        {
            this.approverIds = Lists.arrayList();
            this.itemIds = Lists.arrayList();
            this.issueStatus = IssueStatus.Open;
        }

        public Builder setTitle( final String title )
        {
            this.title = title;
            return this;
        }

        public Builder setDescription( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder setCreatedTime( final Instant createdTime )
        {
            this.createdTime = createdTime;
            return this;
        }

        public Builder setModifiedTime( final Instant modifiedTime )
        {
            this.modifiedTime = modifiedTime;
            return this;
        }

        public Builder setIssueStatus( final IssueStatus issueStatus )
        {
            this.issueStatus = issueStatus;
            return this;
        }

        public Builder setCreator( final PrincipalKey creator )
        {
            this.creator = creator;
            return this;
        }

        public Builder addApproverId( final PrincipalKey approverId )
        {
            this.approverIds.add( approverId );
            return this;
        }

        public Builder addItemId( final ContentId itemId )
        {
            this.itemIds.add( itemId );
            return this;
        }

        public Issue build()
        {
            return new Issue( this );
        }
    }
}
