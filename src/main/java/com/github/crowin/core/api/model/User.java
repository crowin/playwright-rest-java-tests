package com.github.crowin.core.api.model;

import com.github.crowin.core.Config;

public class User {
    public final String username;
    public final String password;

    public User(String username) {
        this.username = Config.get(String.format("users.%s.username", username));
        this.password = Config.get(String.format("users.%s.password", username));
    }
}
