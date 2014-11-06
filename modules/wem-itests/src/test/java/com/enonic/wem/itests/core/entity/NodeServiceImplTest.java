package com.enonic.wem.itests.core.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.core.entity.CreateNodeParams;
import com.enonic.wem.core.entity.Node;
import com.enonic.wem.core.entity.NodeId;
import com.enonic.wem.core.entity.NodeName;
import com.enonic.wem.core.entity.NodePath;
import com.enonic.wem.core.entity.NodeServiceImpl;
import com.enonic.wem.core.entity.RenameNodeParams;
import com.enonic.wem.core.entity.dao.NodeNotFoundException;

import static org.junit.Assert.*;

public class NodeServiceImplTest
    extends AbstractNodeTest
{

    private NodeServiceImpl nodeService;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.nodeService = new NodeServiceImpl();
        this.nodeService.setIndexService( indexService );
        this.nodeService.setQueryService( queryService );
        this.nodeService.setNodeDao( nodeDao );
        this.nodeService.setVersionService( versionService );
        this.nodeService.setWorkspaceService( workspaceService );

        createContentRepository();
    }

    @Test
    public void get_by_id()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node fetchedNode = this.nodeService.getById( NodeId.from( createdNode.id() ) );

        assertEquals( createdNode, fetchedNode );

    }

    @Test(expected = NodeNotFoundException.class)
    public void get_by_id_non_existing()
        throws Exception
    {
        this.nodeService.getById( NodeId.from( "a" ) );

    }


    @Test
    public void rename()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        nodeService.rename( RenameNodeParams.create().
            nodeName( NodeName.from( "my-node-edited" ) ).
            nodeId( createdNode.id() ).
            build() );

        final Node renamedNode = nodeService.getById( createdNode.id() );

        assertEquals( "my-node-edited", renamedNode.name().toString() );

    }


}
