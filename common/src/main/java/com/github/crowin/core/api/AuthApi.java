package com.github.crowin.core.api;

import com.github.crowin.restclient.RestClient;
import com.github.crowin.core.Config;
import org.apache.commons.lang3.tuple.Pair;


import java.util.Map;

public class AuthApi {

    public static Pair<String, String> authenticate(String username, String password) {
        var client = RestClient.create(Config.marketUiUrl());
        var req = Map.of("username", username, "password", password);
        var token = client.post("/users/login")
                .body(req)
                .execute()
                .asJson(Map.class).get("token");
        return Pair.of("Authorization", "Bear " + token);
    }

}
