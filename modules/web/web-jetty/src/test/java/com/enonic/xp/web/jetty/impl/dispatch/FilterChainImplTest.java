package com.enonic.xp.web.jetty.impl.dispatch;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.web.filter.FilterHandler;

public class FilterChainImplTest
{
    private FilterHandler handler;

    private FilterChain chain;

    private HttpServletRequest request;

    private HttpServletResponse response;

    @Before
    public void setup()
    {
        this.handler = Mockito.mock( FilterHandler.class );
        this.chain = new FilterChainImpl( Lists.newArrayList( this.handler ) );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );
    }

    @Test
    public void filter()
        throws Exception
    {
        this.chain.doFilter( this.request, this.response );
        Mockito.verify( this.handler, Mockito.times( 1 ) ).handle( this.request, this.response, this.chain );
    }

    @Test
    public void filter_not_http()
        throws Exception
    {
        final ServletRequest request = Mockito.mock( ServletRequest.class );
        final ServletResponse response = Mockito.mock( ServletResponse.class );

        this.chain.doFilter( request, response );

        Mockito.verify( this.handler, Mockito.times( 0 ) ).handle( Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject() );
    }

    @Test(expected = IOException.class)
    public void filter_error_io()
        throws Exception
    {
        Mockito.doThrow( new IOException() ).when( this.handler ).handle( this.request, this.response, this.chain );
        this.chain.doFilter( this.request, this.response );
    }

    @Test(expected = ServletException.class)
    public void filter_error_servlet()
        throws Exception
    {
        Mockito.doThrow( new ServletException() ).when( this.handler ).handle( this.request, this.response, this.chain );
        this.chain.doFilter( this.request, this.response );
    }

    @Test(expected = ServletException.class)
    public void filter_error_other()
        throws Exception
    {
        Mockito.doThrow( new ArrayIndexOutOfBoundsException() ).when( this.handler ).handle( this.request, this.response, this.chain );
        this.chain.doFilter( this.request, this.response );
    }
}
