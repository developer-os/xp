package com.enonic.wem.portal.underscore;

import java.nio.file.Files;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.common.net.MediaType;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.portal.exception.PortalWebException;
import com.enonic.wem.api.util.MediaTypes;

@Path("{mode}/{path:.+}/_/public/{module}/{resource:.+}")
public final class PublicResource
    extends UnderscoreResource
{
    @PathParam("mode")
    protected String mode;

    @PathParam("path")
    protected String contentPath;

    @PathParam("module")
    protected String moduleName;

    @PathParam("resource")
    protected String resourceName;

    @GET
    public Response getResource()
    {
        final ModuleKey moduleKey = resolveModule( this.contentPath, this.moduleName );
        final ModuleResourceKey moduleResource = ModuleResourceKey.from( moduleKey, "public/" + resourceName );
        final java.nio.file.Path resourceFileSystemPath = modulePathResolver.resolveResourcePath( moduleResource );

        if ( Files.isRegularFile( resourceFileSystemPath ) )
        {
            final String fileName = resourceFileSystemPath.getFileName().toString();
            final MediaType mediaType = MediaTypes.instance().fromFile( fileName );
            return Response.ok( resourceFileSystemPath.toFile() ).type( mediaType.toString() ).build();
        }
        else
        {
            throw PortalWebException.notFound().build();
        }
    }
}
