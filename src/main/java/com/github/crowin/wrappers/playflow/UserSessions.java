package com.github.crowin.wrappers.playflow;

import com.microsoft.playwright.options.Cookie;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserSessions {
    private static final ConcurrentHashMap<String, List<Cookie>> USER_SESSIONS = new ConcurrentHashMap<>();

    public static List<Cookie> get(String user) {
        return USER_SESSIONS.get(user);
    }

    public static void set(String user, List<Cookie> cookies) {
        if (user == null || cookies == null) return;
        USER_SESSIONS.put(user, cookies);
    }
}
