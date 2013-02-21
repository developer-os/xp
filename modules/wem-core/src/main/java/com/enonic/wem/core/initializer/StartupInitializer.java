package com.enonic.wem.core.initializer;

import java.util.List;

import javax.annotation.PostConstruct;

import javax.inject.Inject;
import org.springframework.stereotype.Component;

import com.enonic.wem.core.jcr.loader.JcrInitializer;

@Component
public final class StartupInitializer
{
    private JcrInitializer jcrInitializer;

    private List<InitializerTask> tasks;

    @Inject
    public void setJcrInitializer( final JcrInitializer jcrInitializer )
    {
        this.jcrInitializer = jcrInitializer;
    }

    @Inject
    public void setTasks( final List<InitializerTask> tasks )
    {
        this.tasks = tasks;
    }

    @PostConstruct
    public void initialize()
        throws Exception
    {
        if ( this.jcrInitializer.initialize() )
        {
            initializeTasks();
        }
    }

    private void initializeTasks()
        throws Exception
    {
        for ( final InitializerTask task : this.tasks )
        {
            task.initialize();
        }
    }
}
