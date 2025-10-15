package com.github.crowin;

import lombok.extern.slf4j.Slf4j;
import com.github.crowin.playflow.Playflow;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

@Slf4j
public abstract class TestBase {

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
}
