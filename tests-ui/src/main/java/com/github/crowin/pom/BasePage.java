package com.github.crowin.pom;

import io.qameta.allure.Step;
import com.github.crowin.pom.component.NavigationBar;

public abstract class BasePage {
    private final NavigationBar menu = new NavigationBar();

    @Step("Navigation bar")
    public NavigationBar navigationBar() {
        return menu;
    }

    public abstract void open();
}
