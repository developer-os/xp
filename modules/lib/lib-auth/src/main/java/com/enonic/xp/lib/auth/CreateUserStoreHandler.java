package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.convert.Converters;
import com.enonic.xp.lib.common.UserStoreMapper;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.AuthConfig;
import com.enonic.xp.security.CreateUserStoreParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.UserStoreAccess;
import com.enonic.xp.security.acl.UserStoreAccessControlEntry;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

public final class CreateUserStoreHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private String name;

    private String displayName;

    private String description;

    private ScriptValue authConfig;

    private ScriptValue permissions;

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public void setAuthConfig( final ScriptValue authConfig )
    {
        this.authConfig = authConfig;
    }

    public void setPermissions( final ScriptValue permissions )
    {
        this.permissions = permissions;
    }

    public UserStoreMapper createUserStore()
    {
        final UserStore userstore = this.securityService.get().createUserStore( CreateUserStoreParams.create().
            key( UserStoreKey.from( name )).
            displayName( this.displayName ).
            description( this.description ).
            authConfig( this.newAuthConfig() ).
            permissions( this.newPermissions() ).
            build() );
        return userstore != null ? new UserStoreMapper( userstore, true ) : null;
    }

    private AuthConfig newAuthConfig()
    {
        final Map map = this.authConfig.getMap();
        final String applicationKey = Converters.convert( map.get( "applicationKey" ), String.class );

//        final ArrayList config = Converters.convert( map.get( "config" ), ArrayList.class );
//        final List config = Converters.convert( map.get( "config" ), List.class );

        return new AuthConfig.Builder().
            applicationKey( ApplicationKey.from( applicationKey ) ).
//            config( PropertyTreeJson.fromJson( config ) ).
            build();
    }

    private UserStoreAccessControlList newPermissions()
    {
        final List<ScriptValue> list = this.permissions.getArray();

        return UserStoreAccessControlList.create().addAll( list.stream().map( el -> {
            final Map map = el.getMap();
            return UserStoreAccessControlEntry.create().
                principal( PrincipalKey.from( Converters.convert( map.get( "principal" ), String.class ) ) ).
                access( UserStoreAccess.valueOf( Converters.convert( map.get( "access" ), String.class ) ) ).
                build();
        } ).collect( Collectors.toList() ) ).build();
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
