package com.enonic.xp.lib.node;

import com.enonic.xp.data.PropertyPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueType;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.NodeTypeOld;

import static com.enonic.xp.lib.node.NodePropertyConstants.CHILD_ORDER;
import static com.enonic.xp.lib.node.NodePropertyConstants.INDEX_CONFIG;
import static com.enonic.xp.lib.node.NodePropertyConstants.INHERITS_PERMISSIONS;
import static com.enonic.xp.lib.node.NodePropertyConstants.MANUAL_ORDER_VALUE;
import static com.enonic.xp.lib.node.NodePropertyConstants.NODE_TYPE;
import static com.enonic.xp.lib.node.NodePropertyConstants.PERMISSIONS;

class ModifyNodeExecutor
{
    private final EditableNode editableNode;

    private final PropertyTree propertyTree;

    private ModifyNodeExecutor( final Builder builder )
    {
        editableNode = builder.editableNode;
        propertyTree = builder.propertyTree;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public void execute()
    {
        setSystemData();
        setUserData();
    }

    private void setSystemData()
    {
        if ( exists( propertyTree, CHILD_ORDER, ValueTypes.STRING ) )
        {
            editableNode.childOrder = ChildOrder.from( propertyTree.getString( CHILD_ORDER ) );
        }

        if ( exists( propertyTree, PERMISSIONS, ValueTypes.PROPERTY_SET ) )
        {
            editableNode.permissions = new PermissionsFactory( propertyTree.getSets( PERMISSIONS ) ).create();
        }

        if ( exists( propertyTree, INDEX_CONFIG, ValueTypes.PROPERTY_SET ) )
        {
            editableNode.indexConfigDocument = new IndexConfigFactory( propertyTree.getSet( INDEX_CONFIG ) ).create();
        }

        if ( exists( propertyTree, INHERITS_PERMISSIONS, ValueTypes.BOOLEAN ) )
        {
            editableNode.inheritPermissions = propertyTree.getBoolean( INHERITS_PERMISSIONS );
        }

        if ( exists( propertyTree, MANUAL_ORDER_VALUE, ValueTypes.LONG ) )
        {
            editableNode.manualOrderValue = propertyTree.getLong( MANUAL_ORDER_VALUE );
        }

        if ( exists( propertyTree, NODE_TYPE, ValueTypes.STRING ) )
        {
            editableNode.nodeType = NodeTypeOld.from( propertyTree.getString( NODE_TYPE ) );
        }
    }

    private void setUserData()
    {
        final PropertyTree newPropertyTree = new PropertyTree();

        this.propertyTree.getProperties().forEach( ( property ) -> {
            if ( !property.getName().startsWith( "_" ) )
            {
                property.copyTo( newPropertyTree.getRoot() );
            }
        } );

        this.editableNode.data = newPropertyTree;
    }

    private boolean exists( final PropertyTree propertyTree, final PropertyPath path, final ValueType valueType )
    {
        if ( !propertyTree.hasProperty( path ) )
        {
            return false;
        }

        return propertyTree.getValue( path ).getType().equals( valueType );
    }

    private boolean exists( final PropertyTree propertyTree, final String name, final ValueType valueType )
    {
        return exists( propertyTree, PropertyPath.from( name ), valueType );
    }

    public static final class Builder
    {
        private EditableNode editableNode;

        private PropertyTree propertyTree;

        private Builder()
        {
        }

        public Builder editableNode( final EditableNode val )
        {
            editableNode = val;
            return this;
        }

        public Builder propertyTree( final PropertyTree val )
        {
            propertyTree = val;
            return this;
        }

        public ModifyNodeExecutor build()
        {
            return new ModifyNodeExecutor( this );
        }
    }
}
