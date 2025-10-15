package com.github.crowin.market;

import com.github.crowin.core.api.MarketApi;
import com.github.crowin.pom.ProductsPage;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.*;

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
    @DisplayName( "User can add several products to cart")
    void addProductToCart() {
        var firstProduct = FIRST_USER.api().market().getProducts(0, 8).data().items().getFirst();
        var expectedCount = 2;
        productsPage.product(firstProduct.title()).addToCart(expectedCount);

//        var actualCart = FIRST_USER.api().market().cart();
//
//        Assertions.assertArrayEquals(actualCart.items().get(0).title(), firstProduct.title());
//        Assertions.assertArrayEquals(actualCart.items().get(0).count(), expectedCount);
//        Assertions.assertArrayEquals(actualCart.totalPrice(), firstProduct.price() * 2);
    }
}
