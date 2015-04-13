package com.enonic.xp.portal.impl.controller;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.script.ScriptServiceImpl;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.portal.rendering.RenderResult;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceUrlTestHelper;
import com.enonic.xp.web.servlet.ServletRequestHolder;

public abstract class AbstractControllerTest
{
    protected PostProcessor postProcessor;

    private ControllerScriptFactoryImpl factory;

    protected PortalContext context;

    protected PortalResponse response;

    private final ObjectMapper mapper;

    public AbstractControllerTest()
    {
        this.mapper = new ObjectMapper();
        this.mapper.enable( SerializationFeature.INDENT_OUTPUT );
        this.mapper.enable( SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS );
        this.mapper.enable( SerializationFeature.WRITE_NULL_MAP_VALUES );
    }

    @Before
    public void setup()
        throws Exception
    {
        ResourceUrlTestHelper.mockModuleScheme().modulesClassLoader( getClass().getClassLoader() );

        this.context = new PortalContext();
        this.response = this.context.getResponse();

        this.factory = new ControllerScriptFactoryImpl();
        this.factory.setScriptService( new ScriptServiceImpl() );

        this.postProcessor = Mockito.mock( PostProcessor.class );
        this.factory.setPostProcessor( this.postProcessor );

        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        ServletRequestHolder.setRequest( req );
    }

    protected final void execute( final String script )
    {
        final ControllerScript controllerScript = this.factory.fromScript( ResourceKey.from( script ) );
        controllerScript.execute( this.context );
    }

    protected final String getResponseAsString()
    {
        final PortalResponseSerializer serializer = new PortalResponseSerializer( response );
        final RenderResult result = serializer.serialize();
        return result.getAsString();
    }

    protected final void assertJson( final String name, final String actual )
        throws Exception
    {
        final String resource = "/" + getClass().getName().replace( '.', '/' ) + "-" + name + ".json";
        final URL url = getClass().getResource( resource );

        Assert.assertNotNull( "File [" + resource + "] not found", url );
        final JsonNode expectedJson = this.mapper.readTree( url );
        final JsonNode actualJson = this.mapper.readTree( actual );

        final String expectedStr = this.mapper.writeValueAsString( expectedJson );
        final String actualStr = this.mapper.writeValueAsString( actualJson );

        Assert.assertEquals( expectedStr, actualStr );
    }
}
