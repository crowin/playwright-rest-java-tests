package com.github.crowin.core.api;

import com.github.crowin.core.api.model.User;
import com.github.crowin.wrappers.restclient.RestClient;

public class MarketApi extends BasicApi{

    public MarketApi(RestClient client, User user) {
        super(client, user);
    }
}
