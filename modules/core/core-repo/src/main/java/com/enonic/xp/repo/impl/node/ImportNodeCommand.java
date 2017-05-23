package com.enonic.xp.repo.impl.node;

import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.ImportNodeResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.binary.BinaryService;

public class ImportNodeCommand
    extends AbstractNodeCommand
{
    private final InsertManualStrategy insertManualStrategy;

    private final BinaryAttachments binaryAttachments;

    private final Node importNode;

    private final BinaryService binaryService;

    private final boolean dryRun;

    private final boolean importPermissions;

    private ImportNodeCommand( Builder builder )
    {
        super( builder );
        this.insertManualStrategy = builder.insertManualStrategy;
        this.binaryAttachments = builder.binaryAttachments;
        this.importNode = builder.importNode;
        this.binaryService = builder.binaryService;
        this.dryRun = builder.dryRun;
        this.importPermissions = builder.importPermissions;

    }

    public static Builder create()
    {
        return new Builder();
    }

    public ImportNodeResult execute()
    {
        final boolean exists = CheckNodeExistsCommand.create( this ).
            nodePath( this.importNode.path() ).
            build().
            execute();

        final Node node;
        if ( !exists )
        {
            node = createNode();
        }
        else
        {
            node = updateNode( GetNodeByPathCommand.create( this ).
                nodePath( this.importNode.path() ).
                build().
                execute() );
        }

        return ImportNodeResult.create().
            node( node ).
            preExisting( exists ).
            build();
    }

    private Node createNode()
    {
        if ( this.importNode.isRoot() )
        {
            final CreateRootNodeParams createRootNodeParams = CreateRootNodeParams.create().
                permissions( this.importNode.getPermissions() ).
                childOrder( this.importNode.getChildOrder() ).
                build();

            return CreateRootNodeCommand.create( this ).
                params( createRootNodeParams ).
                build().
                execute();
        }
        else
        {
            final CreateNodeParams createNodeParams = CreateNodeParams.create().
                setNodeId( this.importNode.id() ).
                nodeType( this.importNode.getNodeType() ).
                childOrder( this.importNode.getChildOrder() ).
                setBinaryAttachments( this.binaryAttachments ).
                data( this.importNode.data() ).
                insertManualStrategy( this.insertManualStrategy ).
                manualOrderValue( this.importNode.getManualOrderValue() ).
                name( this.importNode.name().toString() ).
                parent( this.importNode.parentPath() ).
                inheritPermissions( this.importNode.inheritsPermissions() ).
                permissions( this.importNode.getPermissions() ).
                setNodeId( this.importNode.id() ).
                build();

            return CreateNodeCommand.create( this ).
                params( createNodeParams ).
                timestamp( this.importNode.getTimestamp() ).
                binaryService( binaryService ).
                build().
                execute();
        }
    }

    private Node updateNode( final Node existingNode )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            dryRun( this.dryRun ).
            id( existingNode.id() ).
            setBinaryAttachments( this.binaryAttachments ).
            editor( editableNode -> {
                editableNode.data = this.importNode.data();
                if ( this.importPermissions )
                {
                    editableNode.inheritPermissions = this.importNode.inheritsPermissions();
                    editableNode.permissions = this.importNode.getPermissions();
                }
            } ).build();

        return UpdateNodeCommand.create( this ).
            params( updateNodeParams ).
            binaryService( binaryService ).
            build().
            execute();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private InsertManualStrategy insertManualStrategy;

        private BinaryAttachments binaryAttachments;

        private Node importNode;

        private BinaryService binaryService;

        private boolean dryRun;

        private boolean importPermissions;


        private Builder()
        {
        }

        public Builder insertManualStrategy( InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder binaryAttachments( BinaryAttachments binaryAttachments )
        {
            this.binaryAttachments = binaryAttachments;
            return this;
        }

        public Builder importNode( Node importNode )
        {
            this.importNode = importNode;
            return this;
        }

        public Builder binaryBlobStore( BinaryService binaryService )
        {
            this.binaryService = binaryService;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder importPermissions( boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public ImportNodeCommand build()
        {
            return new ImportNodeCommand( this );
        }
    }
}
