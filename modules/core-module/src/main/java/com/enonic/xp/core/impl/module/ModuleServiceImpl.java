package com.enonic.xp.core.impl.module;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleKeys;
import com.enonic.xp.module.ModuleNotFoundException;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.Modules;
import com.enonic.xp.util.Exceptions;

@Component
public final class ModuleServiceImpl
    implements ModuleService
{
    private ModuleRegistry registry;

    private BundleContext bundleContext;

    @Activate
    public void initialize( final ComponentContext context )
    {
        this.bundleContext = context.getBundleContext();
    }

    @Override
    public Module getModule( final ModuleKey key )
        throws ModuleNotFoundException
    {
        final Module module = this.registry.get( key );
        if ( module == null )
        {
            throw new ModuleNotFoundException( key );
        }
        return module;
    }

    @Override
    public Modules getModules( final ModuleKeys keys )
    {
        final ImmutableList.Builder<Module> moduleList = ImmutableList.builder();
        for ( final ModuleKey key : keys )
        {
            final Module module = this.registry.get( key );
            if ( module != null )
            {
                moduleList.add( module );
            }
        }
        return Modules.from( moduleList.build() );
    }

    @Override
    public Modules getAllModules()
    {
        return Modules.from( this.registry.getAll() );
    }

    @Override
    public void installModule( final String url )
    {
        try
        {
            this.bundleContext.installBundle( url );
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public void startModule( final ModuleKey key )
    {
        startModule( getModule( key ) );

    }

    @Override
    public void stopModule( final ModuleKey key )
    {
        stopModule( getModule( key ) );
    }

    @Override
    public void updateModule( final ModuleKey key )
    {
        updateModule( getModule( key ) );
    }

    @Override
    public void uninstallModule( final ModuleKey key )
    {
        uninstallModule( getModule( key ) );
    }

    private void startModule( final Module module )
    {
        try
        {
            module.getBundle().start();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private void stopModule( final Module module )
    {
        try
        {
            module.getBundle().stop();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private void updateModule( final Module module )
    {
        try
        {
            module.getBundle().update();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private void uninstallModule( final Module module )
    {
        try
        {
            module.getBundle().uninstall();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Reference
    public void setRegistry( final ModuleRegistry registry )
    {
        this.registry = registry;
    }
}
