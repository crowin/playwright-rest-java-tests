package com.github.crowin.pom;

import com.microsoft.playwright.Locator;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;
import com.github.crowin.pom.component.Pagination;

import static com.github.crowin.playflow.Playflow.page;

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

    @Step("Get product {productName}")
    public ProductCart product(String productName) {
        return new ProductCart(productCards().getByText(productName));
    }

    @Step("Pagination bar")
    public Pagination pagination() {
        return new Pagination();
    }

    @Override
    @Step("Open Products page")
    public void open() {
        page().navigate("/products");
        log.info("Opened Products page");
    }

    public class ProductCart {

        private final Locator product;

        public ProductCart(Locator product) {
            this.product = product;
        }

        @Step("Product name")
        public Locator name() {
            return product.locator(".font-semibold");
        }

        @Step("Product price")
        public Locator price() {
            return product.locator(".text-slate-600");
        }

        @Step("Product quantity")
        public Locator quantity() {
            return product.getByTestId("product-qty");
        }

        @Step("Product add button")
        public Locator addBtn() {
            return product.getByTestId("product-add");
        }

        @Step("Add product to cart")
        public void addToCart(Integer quantity) {
            this.quantity().fill(String.valueOf(quantity));
            this.addBtn().click();
        }
    }
}