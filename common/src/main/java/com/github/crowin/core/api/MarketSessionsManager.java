package com.github.crowin.core.api;

import com.github.crowin.core.api.model.User;
import org.apache.commons.lang3.tuple.Pair;

import java.util.concurrent.ConcurrentHashMap;

public class MarketSessionsManager {
    private static final ConcurrentHashMap<String, Pair<String, String>> sessions = new ConcurrentHashMap<>();

    private MarketSessionsManager() {}

    public static Pair<String, String> getTokenHeader(User user) {
        if (user == null) return null;

        return sessions.computeIfAbsent(user.username, _ -> AuthApi.authenticate(user.username, user.password));
    }
}
