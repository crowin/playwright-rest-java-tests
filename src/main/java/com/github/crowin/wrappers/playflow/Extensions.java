package com.github.crowin.wrappers.playflow;

import com.microsoft.playwright.options.Cookie;

import java.util.Map;

import static com.github.crowin.wrappers.playflow.Playflow.THREAD_CONTEXT;
import static com.github.crowin.wrappers.playflow.Playflow.THREAD_PAGE;


public class Extensions {

    public void initSession(String user, Map<String, String> userCredentials, SessionClient client) {
        var session = UserSessions.get(user);
        if (session == null || session.isEmpty()) {
            try {
                var cookies = client.initSession(userCredentials);
                if (cookies != null && !cookies.isEmpty()) {
                    session = cookies
                            .entrySet()
                            .stream()
                            .map(entry -> new Cookie(entry.getKey(), entry.getValue()).setUrl(Config.baseUrl()))
                            .toList();
                    UserSessions.set(user, session);
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize session for user: " + user, e);
            }
        }

        if (session != null && !session.isEmpty()) {
            THREAD_CONTEXT.get().addCookies(session);
            THREAD_PAGE.get().reload();
        }
    }

    public void clearSession() {
        THREAD_CONTEXT.get().clearCookies();
        THREAD_PAGE.get().reload();
    }
}
