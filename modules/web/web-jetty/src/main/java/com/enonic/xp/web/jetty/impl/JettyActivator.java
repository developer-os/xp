package com.enonic.xp.web.jetty.impl;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletContext;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.jetty.impl.dispatch.DispatchServlet;

@Component(immediate = true, service = JettyController.class, configurationPid = "com.enonic.xp.web.jetty")
public final class JettyActivator
    implements JettyController
{
    private BundleContext context;

    protected JettyService service;

    private JettyConfig config;

    private ServiceRegistration controllerReg;

    private ServiceRegistration servletContextReg;

    private DispatchServlet dispatchServlet;

    public JettyActivator()
    {
        System.setProperty( "org.apache.felix.http.shared_servlet_context_attributes", "true" );
    }

    @Activate
    public void activate( final BundleContext context, final JettyConfig config )
        throws Exception
    {
        this.context = context;
        fixJettyVersion();

        this.config = config;
        this.service = new JettyService();
        this.service.config = this.config;

        publishController();
        publishServletContext();

        this.service.dispatcherServlet = this.dispatchServlet;
        this.service.start();
    }

    @Deactivate
    public void deactivate()
        throws Exception
    {
        this.controllerReg.unregister();
        this.servletContextReg.unregister();

        this.service.stop();
    }

    private void fixJettyVersion()
    {
        final Dictionary<String, String> headers = this.context.getBundle().getHeaders();
        final String version = headers.get( "X-Jetty-Version" );

        if ( version != null )
        {
            System.setProperty( "jetty.version", version );
        }
    }

    @Override
    public ServletContext getServletContext()
    {
        return this.service.context.getServletHandler().getServletContext();
    }

    private void publishController()
    {
        final Hashtable<String, Object> map = new Hashtable<>();
        map.put( "http.enabled", this.config.http_enabled() );
        map.put( "http.port", this.config.http_port() );

        this.controllerReg = this.context.registerService( JettyController.class, this, map );
    }

    private void publishServletContext()
    {
        final Hashtable<String, Object> map = new Hashtable<>();
        this.servletContextReg = this.context.registerService( ServletContext.class, new ServiceFactory<ServletContext>()
        {
            @Override
            public ServletContext getService( final Bundle bundle, final ServiceRegistration<ServletContext> registration )
            {
                return getServletContext();
            }

            @Override
            public void ungetService( final Bundle bundle, final ServiceRegistration<ServletContext> registration,
                                      final ServletContext service )
            {
                // Do nothing
            }
        }, map );
    }

    @Reference
    public void setDispatchServlet( final DispatchServlet dispatchServlet )
    {
        this.dispatchServlet = dispatchServlet;
    }
}
