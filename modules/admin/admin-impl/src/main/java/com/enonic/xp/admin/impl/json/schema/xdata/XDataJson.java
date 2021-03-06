package com.enonic.xp.admin.impl.json.schema.xdata;

import java.time.Instant;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.admin.impl.json.ItemJson;
import com.enonic.xp.admin.impl.json.form.FormJson;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.schema.mixin.MixinIconUrlResolver;
import com.enonic.xp.schema.xdata.XData;

public class XDataJson
    implements ItemJson
{
    private final XData xData;

    private final Boolean isOptional;

    private final LocaleMessageResolver localeMessageResolver;

    public XDataJson( final Builder builder )
    {
        Preconditions.checkNotNull( builder.localeMessageResolver );

        this.xData = builder.xData;
        this.isOptional = builder.isOptional;
        this.localeMessageResolver = builder.localeMessageResolver;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getName()
    {
        return xData.getName() != null ? xData.getName().toString() : null;
    }

    public String getDisplayName()
    {
        if ( StringUtils.isNotBlank( xData.getDisplayNameI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( xData.getDisplayNameI18nKey(), xData.getDisplayName() );
        }
        else
        {
            return xData.getDisplayName();
        }
    }

    public String getDescription()
    {
        if ( StringUtils.isNotBlank( xData.getDescriptionI18nKey() ) )
        {
            return localeMessageResolver.localizeMessage( xData.getDescriptionI18nKey(), xData.getDescription() );
        }
        else
        {
            return xData.getDescription();
        }
    }

    public Instant getCreatedTime()
    {
        return xData.getCreatedTime();
    }

    public Instant getModifiedTime()
    {
        return xData.getModifiedTime();
    }

    public FormJson getForm()
    {
        return new FormJson( xData.getForm(), this.localeMessageResolver );
    }

    public String getCreator()
    {
        return xData.getCreator() != null ? xData.getCreator().toString() : null;
    }

    public String getModifier()
    {
        return xData.getModifier() != null ? xData.getModifier().toString() : null;
    }

    public Boolean getIsOptional()
    {
        return isOptional;
    }

    @Override
    public boolean getDeletable()
    {
        return false;
    }

    @Override
    public boolean getEditable()
    {
        return false;
    }

    public static final class Builder
    {
        private XData xData;

        private Boolean isOptional = false;

        private MixinIconUrlResolver iconUrlResolver;

        private LocaleMessageResolver localeMessageResolver;

        private Builder()
        {
        }

        public Builder setXData( final XData xData )
        {
            this.xData = xData;
            return this;
        }

        public Builder setOptional( final Boolean optional )
        {
            isOptional = optional;
            return this;
        }

        public Builder setIconUrlResolver( final MixinIconUrlResolver iconUrlResolver )
        {
            this.iconUrlResolver = iconUrlResolver;
            return this;
        }

        public Builder setLocaleMessageResolver( final LocaleMessageResolver localeMessageResolver )
        {
            this.localeMessageResolver = localeMessageResolver;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( localeMessageResolver );
            Preconditions.checkNotNull( iconUrlResolver );
        }

        public XDataJson build()
        {
            validate();
            return new XDataJson( this );
        }
    }

}
