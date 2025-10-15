package com.github.crowin.pom;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import static com.github.crowin.playflow.Playflow.page;

@Slf4j
public class LoginPage extends BasePage {

    @Step("Login as {user}")
    public void login(String user, String password) {
        page().getByTestId("username").fill(user);
        page().getByTestId("password").fill(password);
        page().getByTestId("login").click();
        log.info("Logged in as {}", user);
    }

    @Step("Error message tooltip")
    public Locator errorMsgTooltip() {
        return page().getByTestId("error-msg-tooltip");
    }

    @Override
    @Step("Open Login page")
    public void open() {
        page().navigate("/login");
    }
}
