package com.enonic.wem.api.security.acl;

import org.junit.Test;

import com.google.common.collect.Iterables;

import com.enonic.wem.api.security.PrincipalKey;

import static org.junit.Assert.*;

public class AccessControlEntryTest
{

    @Test
    public void testAccessControlEntry()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.CREATE ).
            allow( Permission.DELETE ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            deny( Permission.WRITE_PERMISSIONS ).
            build();

        assertEquals( PrincipalKey.ofAnonymous(), ace.getPrincipal() );
        assertEquals( "system:user:anonymous[+create, -modify, +delete, +read_permissions, -write_permissions]", ace.toString() );
        assertFalse( ace.isAllowed( Permission.MODIFY ) );
        assertTrue( ace.isDenied( Permission.MODIFY ) );
    }

    @Test
    public void testNoPermissions()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            build();

        assertEquals( PrincipalKey.ofAnonymous(), ace.getPrincipal() );
        assertEquals( "system:user:anonymous[]", ace.toString() );
        assertTrue( ace.isDenied( Permission.MODIFY ) );
        assertTrue( ace.isDenied( Permission.CREATE ) );
        assertEquals( 0, Iterables.size( ace.getAllowedPermissions() ) );
        assertEquals( 0, Iterables.size( ace.getDeniedPermissions() ) );
    }

    @Test
    public void testEquals()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.CREATE ).
            allow( Permission.DELETE ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            deny( Permission.WRITE_PERMISSIONS ).
            build();

        final AccessControlEntry ace2 = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            deny( Permission.WRITE_PERMISSIONS ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            allow( Permission.DELETE ).
            allow( Permission.CREATE ).
            build();

        assertEquals( ace, ace2 );
        assertEquals( ace.hashCode(), ace2.hashCode() );
    }

    @Test
    public void testCopy()
    {
        final AccessControlEntry ace = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.CREATE ).
            allow( Permission.DELETE ).
            allow( Permission.READ_PERMISSIONS ).
            deny( Permission.MODIFY ).
            deny( Permission.WRITE_PERMISSIONS ).
            build();

        final AccessControlEntry newAce = AccessControlEntry.newACE( ace ).
            principal( PrincipalKey.ofAnonymous() ).
            remove( Permission.WRITE_PERMISSIONS ).
            remove( Permission.READ_PERMISSIONS ).
            deny( Permission.DELETE ).
            deny( Permission.CREATE ).
            build();

        assertEquals( "system:user:anonymous[-create, -modify, -delete]", newAce.toString() );
    }

}