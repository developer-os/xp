package com.enonic.xp.web.jetty.impl.dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.servlet.ServletRequestHolder;

@Component
public final class DispatchServletImpl
    extends HttpServlet
    implements DispatchServlet
{
    private DispatchHandler dispatchHandler;

    @Override
    protected void service( final HttpServletRequest req, final HttpServletResponse res )
        throws ServletException, IOException
    {
        ServletRequestHolder.setRequest( req );

        try
        {
            this.dispatchHandler.dispatch( req, res );
        }
        finally
        {
            ServletRequestHolder.setRequest( null );
        }
    }

    @Reference
    public void setDispatchHandler( final DispatchHandler dispatchHandler )
    {
        this.dispatchHandler = dispatchHandler;
    }
}
