package com.enonic.wem.admin.rpc.userstore;

import java.util.List;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.userstore.UserStore;
import com.enonic.wem.api.userstore.UserStores;

class GetAllUserStoresJsonResult
    extends AbstractUserStoreJsonResult
{
    private UserStores userStores;

    public GetAllUserStoresJsonResult( UserStores userStores )
    {
        super();
        this.userStores = userStores;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        json.put( "total", userStores.getSize() );
        json.put( "userStores", serialize( userStores.getList() ) );
    }

    private ArrayNode serialize( final List<UserStore> userStoreList )
    {
        final ArrayNode jsonUserStores = arrayNode();
        for ( UserStore userStore : userStoreList )
        {
            serializeUserStore( jsonUserStores.addObject(), userStore );
        }
        return jsonUserStores;
    }


}
