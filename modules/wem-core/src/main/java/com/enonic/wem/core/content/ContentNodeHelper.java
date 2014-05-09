package com.enonic.wem.core.content;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentPaths;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;

public class ContentNodeHelper
{
    final static String CONTENT_ROOT_NODE_NAME = "content";

    final static NodePath CONTENT_ROOT_NODE = NodePath.newNodePath( NodePath.ROOT, CONTENT_ROOT_NODE_NAME ).build();

    public static NodePath translateContentPathToNodePath( final ContentPath contentPath )
    {
        return new NodePath( CONTENT_ROOT_NODE_NAME + "/" + contentPath.toString() ).asAbsolute();
    }

    public static NodePaths translateContentPathsToNodePaths( final ContentPaths contentPaths )
    {
        final NodePaths.Builder builder = NodePaths.newNodePaths();
        for ( final ContentPath contentPath : contentPaths )
        {
            builder.addNodePath( ContentNodeHelper.translateContentPathToNodePath( contentPath ) );
        }

        return builder.build();
    }

}


