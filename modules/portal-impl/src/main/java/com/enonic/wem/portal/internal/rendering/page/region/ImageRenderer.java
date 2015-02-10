package com.enonic.wem.portal.internal.rendering.page.region;

import java.text.MessageFormat;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.page.region.ImageComponent;
import com.enonic.wem.portal.internal.controller.PortalResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;
import com.enonic.wem.portal.internal.rendering.Renderer;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;

import static com.enonic.wem.portal.internal.rendering.RenderingConstants.PORTAL_COMPONENT_ATTRIBUTE;

@Component(immediate = true, service = Renderer.class)
public final class ImageRenderer
    implements Renderer<ImageComponent>
{
    private static final String EMPTY_IMAGE_HTML = "<figure " + PORTAL_COMPONENT_ATTRIBUTE + "=\"{0}\"></figure>";

    private PortalUrlService urlService;

    @Override
    public Class<ImageComponent> getType()
    {
        return ImageComponent.class;
    }

    @Override
    public RenderResult render( final ImageComponent component, final PortalContext context )
    {
        final RenderMode renderMode = getRenderingMode( context );
        final PortalResponse response = context.getResponse();
        response.setContentType( "text/html" );
        response.setPostProcess( false );

        final StringBuilder html = new StringBuilder();

        final String type = component.getType().toString();
        if ( component.getImage() != null )
        {
            final String imageUrl = buildUrl( context, component.getImage() );
            html.append( "<figure " + PORTAL_COMPONENT_ATTRIBUTE + "=\"" + type + "\">" );
            html.append( "<img style=\"width: 100%\" src=\"" ).append( imageUrl ).append( "\"/>" );
            if ( component.hasCaption() )
            {
                html.append( "<figcaption>" ).append( component.getCaption() ).append( "</figcaption>" );
            }
            html.append( "</figure>" );

        }
        else if ( renderMode == RenderMode.EDIT )
        {
            html.append( MessageFormat.format( EMPTY_IMAGE_HTML, type ) );
        }

        response.setBody( html.toString() );
        return new PortalResponseSerializer( response ).serialize();
    }

    private String buildUrl( final PortalContext context, final ContentId id )
    {
        final ImageUrlParams params = new ImageUrlParams().context( context );
        params.id( id.toString() );
        params.filter( "scalewidth(500)" );
        return this.urlService.imageUrl( params );
    }

    private RenderMode getRenderingMode( final PortalContext context )
    {
        return context == null ? RenderMode.LIVE : context.getMode();
    }

    @Reference
    public void setUrlService( final PortalUrlService urlService )
    {
        this.urlService = urlService;
    }
}
