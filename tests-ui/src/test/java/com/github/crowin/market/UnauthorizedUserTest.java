package com.github.crowin.market;

import com.github.crowin.TestBase;
import com.github.crowin.TestHelper;
import com.github.crowin.playflow.Playflow;
import com.github.crowin.pom.ProductsPage;
import com.github.crowin.user.TestUser;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Feature("Unauthorized user interacts to Market pages")
public class UnauthorizedUserTest extends TestBase {
    private final ProductsPage productsPage = new ProductsPage();

    @BeforeAll
    static void clearSession() {
        TestHelper.clearMarketSession();
    }

    @BeforeEach
    void openMainPage() {
        Playflow.open();
    }

    @Test
    @DisplayName( "User sees all product cards on the Product page")
    void verifyAllProductCards() {
        var expectedProductCount = TestUser.UNAUTHORIZED.api().market().getProducts(0, 8);

        assertThat(productsPage.productCards()).hasCount(expectedProductCount.data().items().size());
        assertThat(productsPage.pagination().pagination).containsText(String.valueOf(expectedProductCount.data().totalPages()));
    }

    @Test
    @DisplayName("User sees product card details on the Products page")
    void verifyProductCardDetails() {
        var expectedFirstProduct = TestUser.UNAUTHORIZED.api().market().getProducts(0, 8).data().items().getFirst();
        var actualProduct = productsPage.product(expectedFirstProduct.title());

        assertThat(actualProduct.price()).hasText("Price: " + expectedFirstProduct.price());
        assertThat(actualProduct.quantity()).not().isVisible();
        assertThat(actualProduct.addBtn()).not().isVisible();
    }

    @Test
    @DisplayName("User see public menu items on Navigation bar")
    void verifyNavigationMenuItems() {
        assertThat(productsPage.navigationBar().login()).isVisible();
        assertThat(productsPage.navigationBar().logout()).not().isVisible();
        assertThat(productsPage.navigationBar().orders()).not().isVisible();
        assertThat(productsPage.navigationBar().products()).isVisible();
        assertThat(productsPage.navigationBar().cart()).not().isVisible();
    }
}
