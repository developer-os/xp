package com.enonic.xp.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FilterHandler
{
    int getOrder();

    void handle( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        throws Exception;
}
