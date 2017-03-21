package com.enonic.xp.web.jetty.impl.dispatch;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.filter.FilterHandler;

import static org.junit.Assert.*;

public class DispatchHandlerReporterTest
{
    private FilterHandler newHandler( final int order )
    {
        final FilterHandler handler = Mockito.mock( FilterHandler.class );
        Mockito.when( handler.getOrder() ).thenReturn( order );
        return handler;
    }

    @Test
    public void testReport()
    {
        final DispatchHandlerImpl dispatchHandler = new DispatchHandlerImpl();
        dispatchHandler.addHandler( newHandler( 0 ) );
        dispatchHandler.addHandler( newHandler( 10 ) );

        final DispatchHandlerReporter reporter = new DispatchHandlerReporter();
        reporter.setDispatchHandler( dispatchHandler );

        assertEquals( "http.filterHandler", reporter.getName() );
        assertNotNull( reporter.getReport() );
    }
}
