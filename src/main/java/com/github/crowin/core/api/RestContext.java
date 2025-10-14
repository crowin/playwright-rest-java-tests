package com.github.crowin.core.api;

import com.github.crowin.core.api.model.User;
import com.github.crowin.wrappers.restclient.RestClient;
import com.github.crowin.core.Config;

public class RestContext {
    private final User user;

    public RestContext(User user) {
        this.user = user;
    }

    public MarketApi market() {
        return new MarketApi(RestClient.create(Config.marketBackendUrl()), user);
    }
}
