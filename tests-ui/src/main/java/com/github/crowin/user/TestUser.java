package com.github.crowin.user;

import com.github.crowin.core.api.RestContext;
import com.github.crowin.core.api.model.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TestUser {
    FIRST_USER(new User("first")),
    THIRD_USER(new User("third")),
    UNAUTHORIZED(null);

    @Getter private final User user;

    public RestContext api() {
        return new RestContext(this.getUser());
    }

    @Override
    public String toString() {
        return this.getUser().username;
    }
}
