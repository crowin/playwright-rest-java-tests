package com.github.crowin.core.api;

import com.github.crowin.core.api.model.User;
import com.github.crowin.wrappers.restclient.RestClient;

public class BasicApi {

    private final RestClient client;
    private final User user;

    public BasicApi(RestClient client, User user) {
        this.client = client;
        this.user = user;


    }


}
