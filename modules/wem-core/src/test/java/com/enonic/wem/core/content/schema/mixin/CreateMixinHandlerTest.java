package com.enonic.wem.core.content.schema.mixin;

import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.schema.mixin.CreateMixin;
import com.enonic.wem.api.content.schema.content.form.Input;
import com.enonic.wem.api.content.schema.content.form.inputtype.InputTypes;
import com.enonic.wem.api.content.schema.mixin.Mixin;
import com.enonic.wem.api.content.schema.mixin.QualifiedMixinName;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.core.command.AbstractCommandHandlerTest;
import com.enonic.wem.core.content.schema.mixin.dao.MixinDao;

import static com.enonic.wem.api.content.schema.content.form.Input.newInput;
import static org.junit.Assert.*;

public class CreateMixinHandlerTest
    extends AbstractCommandHandlerTest
{
    private CreateMixinHandler handler;

    private MixinDao mixinDao;


    @Before
    public void setUp()
        throws Exception
    {
        super.initialize();

        mixinDao = Mockito.mock( MixinDao.class );

        handler = new CreateMixinHandler();
        handler.setMixinDao( mixinDao );
    }

    @Test
    public void createMixin()
        throws Exception
    {
        // setup
        final Input age = newInput().name( "age" ).
            type( InputTypes.TEXT_LINE ).build();
        CreateMixin command = Commands.mixin().create().moduleName( ModuleName.from( "myModule" ) ).formItem( age ).displayName( "Age" );

        // exercise
        this.handler.handle( this.context, command );

        // verify
        Mockito.verify( mixinDao, Mockito.atLeastOnce() ).create( Mockito.isA( Mixin.class ), Mockito.any( Session.class ) );
        QualifiedMixinName mixinName = command.getResult();
        assertNotNull( mixinName );
        assertEquals( "myModule:age", mixinName.toString() );
    }

}
