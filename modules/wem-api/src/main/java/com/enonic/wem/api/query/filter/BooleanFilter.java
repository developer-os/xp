package com.enonic.wem.api.query.filter;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class BooleanFilter
    extends Filter
{
    final ImmutableSet<Filter> must;

    final ImmutableSet<Filter> mustNot;

    final ImmutableSet<Filter> should;

    public BooleanFilter( final Builder builder )
    {
        this.must = ImmutableSet.copyOf( builder.must );
        this.mustNot = ImmutableSet.copyOf( builder.mustNot );
        this.should = ImmutableSet.copyOf( builder.should );
    }

    public ImmutableSet<Filter> getMust()
    {
        return must;
    }

    public ImmutableSet<Filter> getMustNot()
    {
        return mustNot;
    }

    public ImmutableSet<Filter> getShould()
    {
        return should;
    }

    public static class Builder
    {
        Set<Filter> must = Sets.newHashSet();

        Set<Filter> mustNot = Sets.newHashSet();

        Set<Filter> should = Sets.newHashSet();

        public Builder must( final Filter filter )
        {
            this.must.add( filter );
            return this;
        }

        public Builder mustNot( final Filter filter )
        {
            this.mustNot.add( filter );
            return this;
        }

        public Builder should( final Filter filter )
        {
            this.should.add( filter );
            return this;
        }

        public BooleanFilter build()
        {
            return new BooleanFilter( this );

        }

    }


}
