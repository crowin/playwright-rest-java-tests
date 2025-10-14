package com.github.crowin.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        var configFile = "rc.properties";
        try (InputStream input = Config.class.getClassLoader()
                .getResourceAsStream(configFile)) {

            if (input == null) {
                throw new RuntimeException(configFile + " not found in resources");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load " + configFile, e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }

    public static String marketUiUrl() {
        return get("playflow.baseUrl");
    }

    public static String marketBackendUrl() {
        return get("market.backend.rul");
    }
}
