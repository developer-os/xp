package com.enonic.xp.web.jetty.impl.dispatch;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class DispatchServletImplTest
{
    private DispatchHandler handler;

    private DispatchServletImpl servlet;

    private HttpServletRequest request;

    private HttpServletResponse response;

    @Before
    public void setup()
    {
        this.handler = Mockito.mock( DispatchHandler.class );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );

        this.servlet = new DispatchServletImpl();
        this.servlet.setDispatchHandler( this.handler );
    }

    @Test
    public void service()
        throws Exception
    {
        this.servlet.service( this.request, this.response );
        Mockito.verify( this.handler, Mockito.times( 1 ) ).dispatch( this.request, this.response );
    }
}
