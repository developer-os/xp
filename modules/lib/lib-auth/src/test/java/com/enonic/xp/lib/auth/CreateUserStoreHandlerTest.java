package com.enonic.xp.lib.auth;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.security.SecurityService;
import com.enonic.xp.testing.ScriptTestSupport;

public class CreateUserStoreHandlerTest
    extends ScriptTestSupport
{
    private SecurityService securityService;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        this.securityService = Mockito.mock( SecurityService.class );
        addService( SecurityService.class, this.securityService );
    }

    @Test
    public void testExamples()
    {
        Mockito.when( securityService.createUserStore( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestUserStore() );
        runScript( "/site/lib/xp/examples/auth/createUserStore.js" );
    }

    @Test
    public void testCreateUserStore()
    {
        Mockito.when( securityService.createUserStore( Mockito.any() ) ).thenReturn( TestDataFixtures.getTestUserStore() );

        runFunction( "/site/test/createUserStore-test.js", "createUserStore" );
    }
}
