package com.github.crowin.wrappers.playflow;

public final class Config {

    private Config() {}

    public static String browserType() {
        return System.getProperty("playflow.browser", "chromium");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(System.getProperty("playflow.headless", "false"));
    }

    public static String defaultTestId() {
        return System.getProperty("playflow.defaultTestId", "data-test-id");
    }

    public static long timeoutMs() {
        return Long.parseLong(System.getProperty("playflow.timeoutMs", "4000"));
    }

    public static long pollingMs() {
        return Long.parseLong(System.getProperty("playflow.pollingMs", "100"));
    }

    public static String locale() {
        return System.getProperty("playflow.locale", "en-US");
    }

    public static String baseUrl() {
        return System.getProperty("playflow.baseUrl", null);
    }
}
