package com.enonic.xp.web.jetty.impl.dispatch;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.filter.FilterHandler;

import static org.junit.Assert.*;

public class DispatchHandlerImplTest
{
    private FilterHandler handler1;

    private FilterHandler handler2;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private DispatchHandlerImpl dispatch;

    @Before
    public void setup()
    {
        this.handler1 = newHandler( 0 );
        this.handler2 = newHandler( 10 );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );

        this.dispatch = new DispatchHandlerImpl();

        this.dispatch.addHandler( this.handler1 );
        this.dispatch.addHandler( this.handler2 );
    }

    private FilterHandler newHandler( final int order )
    {
        final FilterHandler handler = Mockito.mock( FilterHandler.class );
        Mockito.when( handler.getOrder() ).thenReturn( order );
        return handler;
    }

    @Test
    public void dispatch()
        throws Exception
    {
        this.dispatch.dispatch( this.request, this.response );

        verifyHandle( this.handler1, 1 );
        verifyHandle( this.handler2, 0 );
    }

    @Test
    public void dispatch_empty()
        throws Exception
    {
        this.dispatch.removeHandler( this.handler1 );
        this.dispatch.removeHandler( this.handler2 );

        this.dispatch.dispatch( this.request, this.response );

        verifyHandle( this.handler1, 0 );
        verifyHandle( this.handler2, 0 );
    }

    @Test
    public void testIterable()
    {
        final Iterator<FilterHandler> it = this.dispatch.iterator();

        assertEquals( true, it.hasNext() );
        assertSame( this.handler1, it.next() );

        assertEquals( true, it.hasNext() );
        assertSame( this.handler2, it.next() );

        assertEquals( false, it.hasNext() );
    }

    private void verifyHandle( final FilterHandler handler, final int times )
        throws Exception
    {
        Mockito.verify( handler, Mockito.times( times ) ).handle( Mockito.anyObject(), Mockito.anyObject(), Mockito.anyObject() );
    }
}
