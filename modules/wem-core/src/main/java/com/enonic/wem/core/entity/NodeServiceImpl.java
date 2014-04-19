package com.enonic.wem.core.entity;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.inject.Inject;
import javax.jcr.Session;

import com.enonic.wem.api.entity.CreateNodeParams;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.NodeService;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.api.entity.UpdateNodeParams;
import com.enonic.wem.core.entity.dao.NodeJcrDao;
import com.enonic.wem.core.index.IndexService;
import com.enonic.wem.core.jcr.provider.JcrSessionProvider;
import com.enonic.wem.api.util.Exceptions;

public class NodeServiceImpl
    implements NodeService
{
    @Inject
    private IndexService indexService;

    @Inject
    private JcrSessionProvider jcrSessionProvider;

    @Override
    public Node create( final CreateNodeParams params )
    {
        final Lock locker = concurrencyLock.getLock( jcrSessionProvider );
        locker.lock();

        final Session session = getNewSession();
        try
        {
            return CreateNodeCommand.create().
                params( params ).
                indexService( this.indexService ).
                session( session ).
                build().
                execute();
        }
        finally
        {
            session.logout();
            locker.unlock();
        }
    }

    @Override
    public Node update( final UpdateNodeParams params )
    {
        final Lock locker = concurrencyLock.getLock( jcrSessionProvider );
        locker.lock();

        final Session session = getNewSession();
        try
        {
            return UpdateNodeCommand.create().
                params( params ).
                indexService( this.indexService ).
                session( session ).
                build().
                execute();
        }
        finally
        {
            session.logout();
            locker.unlock();
        }

    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        final Lock locker = concurrencyLock.getLock( jcrSessionProvider );
        locker.lock();

        final Session session = getNewSession();
        try
        {
            return new RenameNodeCommand().params( params ).indexService( this.indexService ).session( session ).execute();
        }
        finally
        {
            session.logout();
            locker.unlock();
        }
    }

    @Override
    public Node getById( final EntityId id )
    {
        final Session session = getNewSession();
        try
        {
            return new NodeJcrDao( session ).getNodeById( id );
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Nodes getByIds( final EntityIds ids )
    {
        final Session session = getNewSession();
        try
        {
            return new GetNodesByIdsCommand().entityIds( ids ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        final Session session = getNewSession();
        try
        {
            return new NodeJcrDao( session ).getNodeByPath( path );
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        final Session session = getNewSession();
        try
        {
            return new GetNodesByPathsCommand().nodePaths( paths ).session( session ).execute();
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Nodes getByParent( final NodePath parent )
    {
        final Session session = getNewSession();
        try
        {
            return new NodeJcrDao( session ).getNodesByParentPath( parent );
        }
        finally
        {
            session.logout();
        }
    }

    @Override
    public Node deleteById( final EntityId id )
    {
        final Lock locker = concurrencyLock.getLock( jcrSessionProvider );
        locker.lock();

        final Session session = getNewSession();
        try
        {
            return new DeleteNodeByIdCommand().entityId( id ).indexService( this.indexService ).session( session ).execute();
        }
        finally
        {
            session.logout();
            locker.unlock();
        }
    }

    @Override
    public Node deleteByPath( final NodePath path )
    {
        final Lock locker = concurrencyLock.getLock( jcrSessionProvider );
        locker.lock();

        final Session session = getNewSession();
        try
        {
            return DeleteNodeByPathCommand.create().
                nodePath( path ).
                indexService( this.indexService ).
                session( session ).
                build().
                execute();
        }
        finally
        {
            session.logout();
            locker.unlock();
        }
    }

    private Session getNewSession()
    {
        try
        {
            return this.jcrSessionProvider.login();
        }
        catch ( final Exception e )
        {
            throw Exceptions.newRutime( "Error creating new JCR session" ).withCause( e );
        }
    }

    private static GenericConcurrencyLock<JcrSessionProvider> concurrencyLock = GenericConcurrencyLock.create();

    private static class GenericConcurrencyLock<T>
    {
        private final Map<T, WeakReference<Lock>> lockMap = new HashMap<>();

        private static <T> GenericConcurrencyLock<T> create()
        {
            return new GenericConcurrencyLock<>();
        }

        Lock getLock( T key )
        {
            Lock lock;
            synchronized ( lockMap )
            {
                lock = getLockFromMap( key );
                if ( lock == null )
                {
                    lock = new ReentrantLock();
                    WeakReference<Lock> value = new WeakReference<Lock>( lock );
                    lockMap.put( key, value );
                }
            }

            return lock;
        }

        private Lock getLockFromMap( T key )
        {
            WeakReference<Lock> weakReference = lockMap.get( key );
            if ( weakReference == null )
            {
                return null;
            }
            return weakReference.get();
        }
    }
}
