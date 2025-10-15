package com.github.crowin;

import com.github.crowin.core.api.MarketSessionsManager;
import com.github.crowin.playflow.Config;
import com.github.crowin.user.TestUser;
import com.microsoft.playwright.options.Cookie;
import io.qameta.allure.Step;

import java.util.List;

import static com.github.crowin.playflow.Playflow.THREAD_CONTEXT;
import static com.github.crowin.playflow.Playflow.THREAD_PAGE;

public class TestHelper {

    @Step("Initialize session for user {user}")
    public static void initMarketSession(TestUser user) {
        var tokenHeader = MarketSessionsManager.getTokenHeader(user.getUser());

        var tokenCookie = new Cookie(tokenHeader.getKey(), tokenHeader.getValue()).setUrl(Config.baseUrl());
        THREAD_CONTEXT.get().addCookies(List.of(tokenCookie));
        THREAD_PAGE.get().reload();
    }

    @Step("Clear session")
    public static void clearMarketSession() {
        THREAD_CONTEXT.get().clearCookies();
        THREAD_PAGE.get().reload();
    }
}
