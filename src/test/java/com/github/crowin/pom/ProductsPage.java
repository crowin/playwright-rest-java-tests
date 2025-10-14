package com.github.crowin.pom;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import com.github.crowin.pom.component.Pagination;

import static com.github.crowin.wrappers.playflow.Playflow.page;

@Slf4j
public class ProductsPage extends BasePage {

    @Step("Products header")
    public Locator header() {
        return page().locator("h1");
    }

    @Step("Product cards")
    public Locator productCards() {
        return page().getByTestId("product-card");
    }

    @Step("Product {productName}")
    public Locator product(String productName) {
        return productCards().getByText(productName);
    }

    @Step("Pagination bar")
    public Pagination pagination() {
        return new Pagination();
    }

    @Step("Add {productName} x {quantity} to cart")
    public ProductsPage addToCart(String productName, Integer quantity) {
        var product = product(productName);
        product.getByTestId("product-qty").fill(String.valueOf(quantity));
        product.getByTestId("product-add").click();
        log.info("Added {} x {} to cart", productName, quantity);
        return this;
    }

    @Override
    @Step("Open Products page")
    public void open() {
        page().navigate("/products");
        log.info("Opened Products page");
    }
}