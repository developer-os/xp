package com.enonic.wem.itests.core.elasticsearch;

import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchDao;
import com.enonic.wem.repo.internal.elasticsearch.ElasticsearchIndexService;

public abstract class AbstractElasticsearchIntegrationTest
{
    protected ElasticsearchDao elasticsearchDao;

    protected ElasticsearchIndexService elasticsearchIndexService;

    private EmbeddedElasticsearchServer server;

    protected Client client;

    private final static Logger LOG = LoggerFactory.getLogger( AbstractElasticsearchIntegrationTest.class );

    @Before
    public void setUp()
        throws Exception
    {
        server = new EmbeddedElasticsearchServer();

        this.client = server.getClient();
        this.elasticsearchDao = new ElasticsearchDao();
        this.elasticsearchDao.setClient( client );

        this.elasticsearchIndexService = new ElasticsearchIndexService();
        elasticsearchIndexService.setElasticsearchDao( elasticsearchDao );
        elasticsearchIndexService.setClient( client );
    }

    public void waitForClusterHealth()
    {
        elasticsearchIndexService.getClusterHealth( TimeValue.timeValueSeconds( 10 ) );
    }

    protected final RefreshResponse refresh()
    {
        RefreshResponse actionGet = client.admin().indices().prepareRefresh().execute().actionGet();
        return actionGet;
    }

    @After
    public void cleanUp()
    {
        LOG.info( "Shutting down" );
        this.client.close();
        server.shutdown();
    }


}
