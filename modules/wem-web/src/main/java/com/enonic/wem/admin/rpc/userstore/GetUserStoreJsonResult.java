package com.enonic.wem.admin.rpc.userstore;

import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.userstore.UserStore;

class GetUserStoreJsonResult
    extends AbstractUserStoreJsonResult
{
    private UserStore userStore;

    public GetUserStoreJsonResult( UserStore userStore )
    {
        super();
        this.userStore = userStore;
    }

    @Override
    protected void serialize( final ObjectNode json )
    {
        serializeUserStore( json, userStore );
    }
}
