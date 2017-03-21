package com.enonic.xp.web.jetty.impl.dispatch;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;

import com.enonic.xp.web.filter.FilterHandler;

final class FilterChainImpl
    implements FilterChain
{
    private final UnmodifiableIterator<FilterHandler> handlers;

    FilterChainImpl( final Iterable<FilterHandler> handlers )
    {
        this.handlers = ImmutableList.copyOf( handlers ).iterator();
    }

    @Override
    public void doFilter( final ServletRequest request, final ServletResponse response )
        throws IOException, ServletException
    {
        if ( ( request instanceof HttpServletRequest ) && ( response instanceof HttpServletResponse ) )
        {
            doFilter( (HttpServletRequest) request, (HttpServletResponse) response );
        }
    }

    private void doFilter( final HttpServletRequest request, final HttpServletResponse response )
        throws IOException, ServletException
    {
        if ( this.handlers.hasNext() )
        {
            doHandle( this.handlers.next(), request, response );
        }
    }

    private void doHandle( final FilterHandler handler, final HttpServletRequest request, final HttpServletResponse response )
        throws IOException, ServletException
    {
        try
        {
            handler.handle( request, response, this );
        }
        catch ( final ServletException | IOException e )
        {
            throw e;
        }
        catch ( final Exception e )
        {
            throw new ServletException( e );
        }
    }
}
