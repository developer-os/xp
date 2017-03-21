package com.enonic.xp.web.jetty.impl.dispatch;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.filter.FilterHandler;

@Component(immediate = true, service = StatusReporter.class)
public final class DispatchHandlerReporter
    extends JsonStatusReporter
{
    private DispatchHandler dispatchHandler;

    @Override
    public String getName()
    {
        return "http.filterHandler";
    }

    @Override
    public JsonNode getReport()
    {
        final ArrayNode json = JsonNodeFactory.instance.arrayNode();
        for ( final FilterHandler handler : this.dispatchHandler )
        {
            final ObjectNode node = json.addObject();
            node.put( "order", handler.getOrder() );
            node.put( "class", handler.getClass().getName() );
        }

        return json;
    }

    @Reference
    public void setDispatchHandler( final DispatchHandler dispatchHandler )
    {
        this.dispatchHandler = dispatchHandler;
    }
}
