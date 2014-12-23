package com.enonic.xp.portal.jslib.impl;

import org.junit.Test;

import com.enonic.wem.script.command.CommandHandler;

public class AssetUrlHandlerTest
    extends AbstractHandlerTest
{
    @Override
    protected CommandHandler createHandler()
    {
        return new AssetUrlHandler();
    }

    @Test
    public void createUrl()
        throws Exception
    {
        execute( "createUrl" );
    }
}
