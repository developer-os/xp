package com.enonic.xp.cluster;

public interface ClusterInstance
{
    void removed( final ClusterMember member );

    void added( final ClusterMember member );
}
