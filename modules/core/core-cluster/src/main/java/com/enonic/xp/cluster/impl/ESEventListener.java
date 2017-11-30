package com.enonic.xp.cluster.impl;

import org.elasticsearch.cluster.ClusterChangedEvent;
import org.elasticsearch.cluster.ClusterStateListener;

public class ESEventListener
    implements ClusterStateListener
{
    @Override
    public void clusterChanged( final ClusterChangedEvent event )
    {
        System.out.println( "****** ES ClusterChangedEvent: " + event );
    }
}
