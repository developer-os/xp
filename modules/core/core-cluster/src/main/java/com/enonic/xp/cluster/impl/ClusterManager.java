package com.enonic.xp.cluster.impl;

import java.util.List;

import org.apache.ignite.Ignite;
import org.apache.ignite.events.EventType;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.cluster.ClusterService;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Lists;

import com.enonic.xp.cluster.ClusterInstance;

@Component(immediate = true)
public class ClusterManager
{
    private List<ClusterInstance> clusterInstances = Lists.newArrayList();

    private Client client;

    private Ignite ignite;

    private ClusterService clusterService;

    @Activate
    public void activate( final BundleContext context )
    {

        this.ignite.events().localListen( new IgniteEventListener(), EventType.EVTS_DISCOVERY );
        this.clusterService.add( new ESEventListener() );

        final ClusterStateRequest clusterStateRequest = Requests.clusterStateRequest().
            clear().
            nodes( true ).
            indices( "" );

        final ClusterStateResponse response = this.client.admin().cluster().state( clusterStateRequest ).actionGet();

    }

    @Reference
    public void setClusterService( final ClusterService clusterService )
    {
        this.clusterService = clusterService;
    }

    @Reference
    public void setClient( final Client client )
    {
        this.client = client;
    }

    @Reference
    public void setIgnite( final Ignite ignite )
    {
        this.ignite = ignite;
    }

    @Reference
    public void clusterInstance( final ClusterInstance clusterInstance )
    {
        this.clusterInstances.add( clusterInstance );


    }

}
