package com.github.crowin.playflow;

import java.util.Map;

/**
 * A simple interface that users can implement to initialize an authenticated session
 */
public interface SessionClient {
    /**
     * Implement authentication for the provided user and return cookies to be used in the browser context.
     */
    Map<String, String> initSession(Map<String, String> userCredentials) throws Exception;
}
