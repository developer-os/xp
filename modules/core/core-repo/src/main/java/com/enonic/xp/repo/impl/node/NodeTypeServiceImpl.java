package com.enonic.xp.repo.impl.node;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.node.NodeType;
import com.enonic.xp.node.NodeTypeName;
import com.enonic.xp.node.NodeTypeService;

@Component(immediate = true)
public class NodeTypeServiceImpl
    implements NodeTypeService
{
    private final NodeTypeRegistry registry;

    private final static Logger LOG = LoggerFactory.getLogger( NodeTypeServiceImpl.class );

    public NodeTypeServiceImpl()
    {
        this.registry = new NodeTypeRegistry();
    }

    @Override
    public NodeType get( final NodeTypeName name )
    {
        return this.registry.get( name );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void setNodeType( final NodeType nodeType )
    {
        LOG.info( String.format( "Adding nodeType [%s]", nodeType.getNodeTypeName() ) );
        this.registry.add( nodeType );
    }

}
