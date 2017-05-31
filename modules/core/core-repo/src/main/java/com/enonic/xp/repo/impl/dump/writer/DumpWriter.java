package com.enonic.xp.repo.impl.dump.writer;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repository.RepositoryId;

public interface DumpWriter
{
    void open( final RepositoryId repositoryId, final Branch branch );

    void close();

    void writeMetaData( final DumpEntry dumpEntry );

    void writeVersion( final NodeVersionId nodeVersionId );

    void writeBinary( final BlobKey blobKey, final ByteSource source );

}
