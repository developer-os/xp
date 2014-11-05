package com.enonic.wem.api.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import static org.junit.Assert.*;

public class PrincipalTest
{
    private static final Instant NOW = Instant.ofEpochSecond( 0 );

    private static Clock clock = Clock.fixed( NOW, ZoneId.of( "UTC" ) );

    @Test
    public void testCreateUser()
    {
        final User user = User.create().
            login( "userlogin" ).
            displayName( "my user" ).
            key( PrincipalKey.ofUser( new UserStoreKey( "myuserstore" ), "userid" ) ).
            email( "user@email" ).
            modifiedTime( Instant.now( clock ) ).
            build();

        assertEquals( "userlogin", user.getLogin() );
        assertEquals( "my user", user.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), user.getKey() );
        assertEquals( "user@email", user.getEmail() );

        final User userCopy = User.create( user ).build();
        assertEquals( "userlogin", userCopy.getLogin() );
        assertEquals( "my user", userCopy.getDisplayName() );
        assertEquals( false, userCopy.isDisabled() );
        assertEquals( PrincipalKey.from( "myuserstore:user:userid" ), userCopy.getKey() );
        assertEquals( "user@email", userCopy.getEmail() );
    }

    @Test
    public void testCreateGroup()
    {
        final Group group = Group.create().
            displayName( "my group" ).
            key( PrincipalKey.ofGroup( new UserStoreKey( "myuserstore" ), "groupid" ) ).
            modifiedTime( Instant.now( clock ) ).
            build();

        assertEquals( "my group", group.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:group:groupid" ), group.getKey() );

        final Group groupCopy = Group.create( group ).build();
        assertEquals( "my group", groupCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:group:groupid" ), groupCopy.getKey() );
    }

    @Test
    public void testAnonymous()
    {
        final User anonymous = User.anonymous();

        assertTrue( anonymous.getKey().isAnonymous() );
        assertEquals( "anonymous", anonymous.getDisplayName() );
        assertEquals( PrincipalKey.ofAnonymous(), anonymous.getKey() );
    }

    @Test
    public void testCreateRole()
    {
        final Role role = Role.create().
            displayName( "my role" ).
            key( PrincipalKey.ofRole( "administrators" ) ).
            modifiedTime( Instant.now( clock ) ).
            build();

        assertEquals( "my role", role.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:role:administrators" ), role.getKey() );

        final Role roleCopy = Role.create( role ).build();
        assertEquals( "my role", roleCopy.getDisplayName() );
        assertEquals( PrincipalKey.from( "myuserstore:role:administrators" ), roleCopy.getKey() );
    }

}