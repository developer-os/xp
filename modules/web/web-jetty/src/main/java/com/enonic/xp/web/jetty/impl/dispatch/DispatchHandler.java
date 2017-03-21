package com.enonic.xp.web.jetty.impl.dispatch;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.enonic.xp.web.filter.FilterHandler;

public interface DispatchHandler
    extends Iterable<FilterHandler>
{
    void dispatch( ServletRequest req, ServletResponse res )
        throws ServletException, IOException;
}
