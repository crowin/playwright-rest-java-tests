package com.github.crowin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.crowin.core.api.AuthApi;
import com.github.crowin.user.TestUser;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import com.github.crowin.wrappers.playflow.Playflow;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.util.Map;

@Slf4j
public abstract class TestBase {
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() {
        Playflow.open();
        log.info("Browser opened and ready to use");
    }

    @AfterAll
    static void afterAll() {
        Playflow.quit();
        log.info("Browser closed");
    }

    @Step("Initialize session for user {user}")
    public void initSession(TestUser user) {
        var mappedUser = mapper.convertValue(user.getUser(), new TypeReference<Map<String, String>>() {});
        Playflow.extensions().initSession(user.getUser().username, mappedUser, new AuthApi());
    }

    @Step("Clear session")
    public void clearSession() {
        Playflow.extensions().clearSession();
    }
}
