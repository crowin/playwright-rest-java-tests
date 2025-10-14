package com.github.crowin.pom.component;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import com.github.crowin.wrappers.playflow.Playflow;

@Slf4j
public class NavigationBar {
    private final Locator bar = Playflow.page().getByTestId("header-nav");

    @Step("Products item")
    public Locator products() {
        return bar.getByTestId("products-link");
    }

    @Step("Orders item")
    public Locator orders() {
        return bar.getByTestId("orders-link");
    }

    @Step("Cart item")
    public Locator cart() {
        return bar.getByTestId("cart-link");
    }

    @Step("Login item")
    public Locator login() {
        return bar.getByTestId("login-link");
    }

    @Step("Logout item")
    public Locator logout() {
        return bar.getByTestId("logout-link");
    }
}
