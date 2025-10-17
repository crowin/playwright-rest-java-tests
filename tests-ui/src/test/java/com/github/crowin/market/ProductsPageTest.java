package com.github.crowin.market;

import com.github.crowin.core.api.MarketApi;
import com.github.crowin.pom.ProductsPage;
import io.qameta.allure.Feature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.crowin.TestHelper.initMarketSession;
import static com.github.crowin.user.TestUser.FIRST_USER;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Feature(value = "Products page")
public class ProductsPageTest {
    private final ProductsPage productsPage = new ProductsPage();
    private final MarketApi marketApi = FIRST_USER.api().market();

    @BeforeAll
    public static void initSession() {
        initMarketSession(FIRST_USER);
    }

    @BeforeEach
    public void openMainPage() {
        marketApi.clearCart();
        productsPage.open();
    }

    @Test
    @DisplayName( "User sees all product cards on the Product page")
    void verifyAllProductCards() {
        var expectedProductCount = FIRST_USER.api().market().getProducts(0, 8);

        assertThat(productsPage.productCards()).hasCount(expectedProductCount.data().items().size());
        assertThat(productsPage.pagination().pagination).containsText(String.valueOf(expectedProductCount.data().totalPages()));
    }

    @Test
    @DisplayName( "User can add several items of the one product to cart")
    void addProductOfOneNameToCart() {
        var firstProduct = FIRST_USER.api().market().getProducts(0, 8).data().items().getFirst();
        var expectedCount = 2;
        productsPage.product(firstProduct.title()).addToCart(expectedCount);

        var actualCart = FIRST_USER.api().market().getCart().data();

        Assertions.assertThat(actualCart.items().getFirst().product().title())
                .as("Product has a unexpected title in the cart")
                .isEqualTo(firstProduct.title());
        Assertions.assertThat(actualCart.items().getFirst().quantity())
                .as("Product has a unexpected quantity in the cart")
                .isEqualTo(expectedCount);
    }

    @Test
    @DisplayName( "User can add several items of the different products to cart")
    void addDifferentProductsToCart() {
        var products = FIRST_USER.api().market().getProducts(0, 8).data().items();
        var firstProduct = products.get(0);
        var secondProduct = products.get(1);
        var expectedFirstProductCount = 1;
        var expectedSecondProductCount = 3;

        productsPage.product(firstProduct.title()).addToCart(1);
        productsPage.product(secondProduct.title()).addToCart(1);

        var actualCart = FIRST_USER.api().market().getCart().data();

        Assertions.assertThat(actualCart.items().get(0).product().title())
                .as("The first product has a unexpected title in the cart")
                .isEqualTo(firstProduct.title());
        Assertions.assertThat(actualCart.items().get(0).quantity())
                .as("The first product has a unexpected quantity in the cart")
                .isEqualTo(expectedFirstProductCount);
        Assertions.assertThat(actualCart.items().get(1).product().title())
                .as("The second product has a unexpected title in the cart")
                .isEqualTo(secondProduct.title());
        Assertions.assertThat(actualCart.items().get(1).quantity())
                .as("The second product has a unexpected quantity in the cart")
                .isEqualTo(expectedSecondProductCount);
    }
}
