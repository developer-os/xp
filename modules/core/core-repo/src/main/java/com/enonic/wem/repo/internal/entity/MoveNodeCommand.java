package com.enonic.wem.repo.internal.entity;

import java.time.Instant;

import com.google.common.base.Preconditions;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.storage.BranchNodeVersion;
import com.enonic.wem.repo.internal.branch.storage.BranchNodeVersions;
import com.enonic.wem.repo.internal.index.query.NodeQueryResult;
import com.enonic.wem.repo.internal.repository.IndexNameResolver;
import com.enonic.wem.repo.internal.search.SearchService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.MoveNodeException;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.SearchMode;
import com.enonic.xp.security.acl.Permission;

public class MoveNodeCommand
    extends AbstractNodeCommand
{
    private final NodeId nodeId;

    private final NodePath newParentPath;

    private final NodeName newNodeName;

    private MoveNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
        this.newParentPath = builder.newParentPath;
        this.newNodeName = builder.newNodeName;
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId );

        final NodeName newNodeName = resolveNodeName( existingNode );

        final NodePath newParentPath = resolvePath( existingNode );

        if ( noChanges( existingNode, newParentPath, newNodeName ) )
        {
            return existingNode;
        }

        checkNotMovedToSelfOrChild( existingNode, newParentPath );

        checkContextUserPermissionOrAdmin( existingNode, newParentPath );

        final Node movedNode = doMoveNode( newParentPath, newNodeName, nodeId );

        indexServiceInternal.refresh( IndexNameResolver.resolveSearchIndexName( ContextAccessor.current().getRepositoryId() ) );

        return movedNode;
    }

    private void checkContextUserPermissionOrAdmin( final Node existingSourceNode, final NodePath newParentPath )
    {
        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.DELETE, existingSourceNode );

        final Node newParentNode = GetNodeByPathCommand.create( this ).
            nodePath( newParentPath ).
            build().
            execute();

        if ( newParentNode == null )
        {
            throw new NodeNotFoundException( "Cannot move node to parent with path '" + newParentPath + "', does not exist" );
        }

        NodePermissionsResolver.requireContextUserPermissionOrAdmin( Permission.CREATE, newParentNode );
    }

    private NodePath resolvePath( final Node existingNode )
    {
        final NodePath newParentPath;
        if ( this.newParentPath == null )
        {
            newParentPath = existingNode.parentPath();
        }
        else
        {
            newParentPath = this.newParentPath;
        }
        return newParentPath;
    }

    private NodeName resolveNodeName( final Node existingNode )
    {
        final NodeName newNodeName;
        if ( this.newNodeName == null )
        {
            newNodeName = existingNode.name();
        }
        else
        {
            newNodeName = this.newNodeName;
        }
        return newNodeName;
    }

    private void checkNotMovedToSelfOrChild( final Node existingNode, final NodePath newParentPath )
    {
        if ( newParentPath.equals( existingNode.path() ) )
        {
            throw new MoveNodeException( "Not allowed to move to " + newParentPath + " because child of self ( " + existingNode.path() );
        }

        if ( newParentPath.getParentPaths().contains( existingNode.path() ) )
        {
            throw new MoveNodeException( "Not allowed to move to " + newParentPath + " because child of self ( " + existingNode.path() );
        }
    }

    private boolean noChanges( final Node existingNode, final NodePath newParentPath, final NodeName newNodeName )
    {
        return existingNode.parentPath().equals( newParentPath ) && existingNode.name().equals( newNodeName );
    }

    private Node doMoveNode( final NodePath newParentPath, final NodeName newNodeName, final NodeId id )
    {
        final Node persistedNode = doGetById( id );

        final NodeQueryResult nodeQueryResult = this.searchService.search( NodeQuery.create().
            parent( persistedNode.path() ).
            from( 0 ).
            size( SearchService.GET_ALL_SIZE_FLAG ).
            searchMode( SearchMode.SEARCH ).
            build(), InternalContext.from( ContextAccessor.current() ) );

        final BranchNodeVersions branchNodeVersions =
            this.storageService.getBranchNodeVersions( nodeQueryResult.getNodeIds(), InternalContext.from( ContextAccessor.current() ) );

        final NodeName nodeName = ( newNodeName != null ) ? newNodeName : persistedNode.name();

        verifyNoExistingAtNewPath( newParentPath, newNodeName );

        final Node.Builder nodeToMoveBuilder = Node.create( persistedNode ).
            name( nodeName ).
            parentPath( newParentPath ).
            indexConfigDocument( persistedNode.getIndexConfigDocument() ).
            timestamp( Instant.now() );

        final Node movedNode;

        // The node that is moved must be updated
        if ( persistedNode.id().equals( this.nodeId ) )
        {
            final boolean isRenaming = newParentPath.equals( persistedNode.parentPath() );

            if ( !isRenaming )
            {
                // when moving a Node "inheritPermissions" must be set to false so the permissions are kept with the transfer
                nodeToMoveBuilder.inheritPermissions( false );
            }

            movedNode = StoreNodeCommand.create( this ).
                node( nodeToMoveBuilder.build() ).
                updateMetadataOnly( false ).
                build().
                execute();
        }
        else
        {
            movedNode = StoreNodeCommand.create( this ).
                updateMetadataOnly( true ).
                node( nodeToMoveBuilder.build() ).
                build().
                execute();
        }

        for ( final BranchNodeVersion branchNodeVersion : branchNodeVersions )
        {
            doMoveNode( nodeToMoveBuilder.build().path(), getNodeName( branchNodeVersion ), branchNodeVersion.getNodeId() );
        }

        return movedNode;
    }

    private NodeName getNodeName( final BranchNodeVersion branchNodeVersion )
    {
        return NodeName.from( branchNodeVersion.getNodePath().getLastElement().toString() );
    }

    private void verifyNoExistingAtNewPath( final NodePath newParentPath, final NodeName newNodeName )
    {
        final NodePath newNodePath = NodePath.create( newParentPath, newNodeName.toString() ).build();

        final boolean exists = CheckNodeExistsCommand.create( this ).
            nodePath( newNodePath ).
            build().
            execute();

        if ( exists )
        {
            throw new NodeAlreadyExistAtPathException( newParentPath );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final AbstractNodeCommand source )
    {
        return new Builder( source );
    }

    public static class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId id;

        private NodePath newParentPath;

        private NodeName newNodeName;

        private Builder()
        {
            super();
        }

        private Builder( final AbstractNodeCommand source )
        {
            super( source );
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public Builder newParent( final NodePath parentNodePath )
        {
            this.newParentPath = parentNodePath;
            return this;
        }

        public Builder newNodeName( final NodeName nodeName )
        {
            this.newNodeName = nodeName;
            return this;
        }

        public MoveNodeCommand build()
        {
            validate();
            return new MoveNodeCommand( this );
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( id );

            if ( this.newParentPath == null && this.newNodeName == null )
            {
                throw new IllegalArgumentException( "Must provide either newNodeName or newParentPath" );
            }

        }
    }

}
