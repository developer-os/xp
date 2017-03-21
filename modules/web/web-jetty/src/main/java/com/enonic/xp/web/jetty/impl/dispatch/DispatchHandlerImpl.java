package com.enonic.xp.web.jetty.impl.dispatch;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.google.common.collect.Lists;

import com.enonic.xp.web.filter.FilterHandler;

@Component
public final class DispatchHandlerImpl
    implements DispatchHandler
{
    private final List<FilterHandler> list = Lists.newCopyOnWriteArrayList();

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addHandler( final FilterHandler handler )
    {
        this.list.add( handler );
        sortList();
    }

    public void removeHandler( final FilterHandler handler )
    {
        this.list.remove( handler );
        sortList();
    }

    private void sortList()
    {
        this.list.sort( this::compare );
    }

    private int compare( final FilterHandler handler1, final FilterHandler handler2 )
    {
        return handler1.getOrder() - handler2.getOrder();
    }

    @Override
    public void dispatch( final ServletRequest req, final ServletResponse res )
        throws ServletException, IOException
    {
        new FilterChainImpl( this.list ).doFilter( req, res );
    }

    @Override
    public Iterator<FilterHandler> iterator()
    {
        return this.list.iterator();
    }
}
