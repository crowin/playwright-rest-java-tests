package com.github.crowin.core.api;

import com.github.crowin.wrappers.restclient.RestClient;
import com.github.crowin.wrappers.playflow.SessionClient;
import com.github.crowin.core.Config;

import java.util.Map;

public class AuthApi implements SessionClient {

    public static Map<String, String> authenticate(String username, String password) {
        var client = RestClient.create(Config.marketUiUrl());
        var req = Map.of("username", username, "password", password);
        var token = client.post("/users/login")
                .body(req)
                .execute()
                .asJson(Map.class).get("token");

        return Map.of("Authorization", "Bear " + token);
    }

    @Override
    public Map<String, String> initSession(Map<String, String> userCredentials) {
        return authenticate(userCredentials.get("username"), userCredentials.get("password"));
    }
}
