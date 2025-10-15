package com.github.crowin.pom.component;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import com.github.crowin.playflow.Playflow;

public class Pagination {
    public final Locator pagination = Playflow.page().getByTestId("pagination");

    @Step("Click next page")
    public Pagination clickNextPage() {
        pagination.getByTestId("next-page").click();
        return this;
    }

    @Step("Click previous page")
    public Pagination clickPreviousPage() {
        pagination.getByTestId("previous-page").click();
        return this;
    }
}
