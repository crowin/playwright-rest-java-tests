package com.github.crowin.market;

import com.github.crowin.TestHelper;
import com.github.crowin.pom.ProductsPage;
import com.github.crowin.user.TestUser;
import io.qameta.allure.Feature;
import com.github.crowin.TestBase;
import com.github.crowin.pom.LoginPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@Feature("Login page tests")
class LoginPageTest extends TestBase {
    private final LoginPage loginPage = new LoginPage();
    private final ProductsPage productsPage = new ProductsPage();

    @BeforeEach
    @DisplayName("Open Login page")
    void openLoginPage() {
        TestHelper.initMarketSession(TestUser.FIRST_USER);
    }

    @AfterEach
    @DisplayName("Clear session")
    void closeUserSession() {
        TestHelper.clearMarketSession();
    }

    @ParameterizedTest(name = "Login as {0} shows Products page")
    @EnumSource(value = TestUser.class, names = {"FIRST_USER", "THIRD_USER"})
    void verifySuccessfulLogin(TestUser user) {
        var data = user.getUser();
        loginPage.open();
        loginPage.login(data.username, data.password);

        assertThat(productsPage.header()).containsText("Products");
        assertThat(productsPage.navigationBar().logout()).isVisible();
    }

    private static Stream<Arguments> provideInvalidLoginData() {
        var user = TestUser.FIRST_USER.getUser();
        var errorMsg = "Invalid username or password";
        return Stream.of(
                Arguments.of(user.username, "wrong-password", errorMsg),
                Arguments.of(user.username, "", errorMsg),
                Arguments.of(user.username, user.password.substring(0, 2), errorMsg),
                Arguments.of("", "", errorMsg)
        );
    }

    @ParameterizedTest(name = "Login shows error message tooltip")
    @MethodSource("provideInvalidLoginData")
    void verifyFailedLogin(String username, String password, String expecterErrorMsg) {
        loginPage.open();
        loginPage.login(username, password);

        assertThat(loginPage.errorMsgTooltip()).isVisible();
        assertThat(loginPage.errorMsgTooltip()).containsText(expecterErrorMsg);
    }

    @Test
    @DisplayName("User can login and logout from Product page")
    void userCanLoginAndLogout() {
        var user = TestUser.FIRST_USER.getUser();

        productsPage.open();
        productsPage.navigationBar().login().click();
        loginPage.login(user.username, user.password);
        productsPage.navigationBar().logout().click();

        assertThat(productsPage.navigationBar().login()).isVisible();
    }
}
