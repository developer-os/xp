package com.enonic.xp.awss3;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.domain.Blob;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeyCreator;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.util.ClassLoaderHelper;
import com.enonic.xp.util.Exceptions;

class AwsS3BlobStore
    implements BlobStore
{
    private final BlobStoreContext context;

    private final org.jclouds.blobstore.BlobStore blobStore;

    private final String accessKey;

    private final String secretAccessKey;

    private final String endpoint;

    private final Map<Segment, String> buckets;


    private AwsS3BlobStore( final Builder builder )
    {
        accessKey = builder.accessKey;
        secretAccessKey = builder.secretAccessKey;
        endpoint = builder.endpoint;
        buckets = builder.buckets;

        this.context = ClassLoaderHelper.callWith( this::createContext, ContextBuilder.class );

        this.blobStore = this.context.getBlobStore();

        verifyOrCreateBucket();
    }

    private void verifyOrCreateBucket()
    {
        for ( final String bucketName : buckets.values() )
        {
            try
            {
                blobStore.createContainerInLocation( null, bucketName );
            }
            catch ( Exception e )
            {
                throw new BlobStoreException( "Cannot create or verify bucket [" + bucketName + "]", e );
            }
        }
    }

    private BlobStoreContext createContext()
    {
        final ContextBuilder contextBuilder = ContextBuilder.newBuilder( "aws-s3" ).
            credentials( this.accessKey, this.secretAccessKey );

        if ( !Strings.isNullOrEmpty( endpoint ) )
        {
            contextBuilder.endpoint( this.endpoint );
        }

        return contextBuilder.buildView( BlobStoreContext.class );
    }

    public static Builder create()
    {
        return new Builder();
    }

    private String getBucketName( final Segment segment )
    {
        final String bucketName = this.buckets.get( segment );

        if ( bucketName == null )
        {
            throw new BlobStoreException( "Bucket not found for segment [" + segment.getValue() + "]" );
        }

        return bucketName;
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {

        final Blob blob = this.blobStore.getBlob( getBucketName( segment ), key.toString() );

        if ( blob == null )
        {
            return null;
        }

        try (final InputStream inputStream = blob.getPayload().openStream())
        {
            final ByteSource source = ByteSource.wrap( ByteStreams.toByteArray( inputStream ) );

            return new AwsS3BlobRecord( source, key );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobKey key = BlobKeyCreator.createKey( in );

        return doAddRecord( segment, in, key );
    }

    private BlobRecord doAddRecord( final Segment segment, final ByteSource in, final BlobKey key )
    {
        try
        {
            final Blob blob = blobStore.blobBuilder( key.toString() ).
                payload( in ).
                contentLength( in.size() ).
                build();

            blobStore.putBlob( getBucketName( segment ), blob );

            return new AwsS3BlobRecord( in, key );
        }
        catch ( IOException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        return doAddRecord( segment, record.getBytes(), record.getKey() );
    }

    public static final class Builder
    {
        private String accessKey;

        private String secretAccessKey;

        private String endpoint;

        private Map<Segment, String> buckets;


        private Builder()
        {
        }

        public Builder accessKey( final String val )
        {
            accessKey = val;
            return this;
        }

        public Builder secretAccessKey( final String val )
        {
            secretAccessKey = val;
            return this;
        }

        public Builder endpoint( final String val )
        {
            endpoint = val;
            return this;
        }

        public Builder setBuckets( final Map<Segment, String> buckets )
        {
            this.buckets = buckets;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.accessKey, "accessKey must be configured" );
            Preconditions.checkNotNull( this.secretAccessKey, "secretAccessKey must be configured" );
            Preconditions.checkNotNull( this.buckets, "bucketName must be configured" );
        }

        public AwsS3BlobStore build()
        {
            this.validate();
            return new AwsS3BlobStore( this );
        }
    }

    void close()
    {
        if ( this.context != null )
        {
            this.context.close();
        }
    }
}
