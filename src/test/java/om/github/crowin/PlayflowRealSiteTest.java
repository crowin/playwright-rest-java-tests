package om.github.crowin;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple real parallel E2E tests: login with two different users and verify the main screen.
 */
@Execution(ExecutionMode.CONCURRENT)
class PlayflowRealSiteTest {

  @BeforeAll
  static void beforeAll() {
    System.setProperty("playflow.headless", "true");
    System.setProperty("playflow.baseUrl", "https://www.saucedemo.com");
  }

  @AfterAll
  static void afterAll() {
    Playflow.quit();
  }

  @BeforeEach
  void openLoginPage() {
    Playflow.open("/");
  }

  @AfterEach
  void resetContext() {
    Playflow.close();
  }

  @ParameterizedTest(name = "Login as {0} shows Products page")
  @ValueSource(strings = {"standard_user", "problem_user"})
  void login_withTwoUsers_checksProductsScreen(String username) {
    var page = Playflow.page();

    // Perform login
    page.locator("#user-name").fill(username);
    page.locator("#password").fill("secret_sauce");
    page.locator("#login-button").click();

    // Verify we are on inventory page and "Products" title is visible
    assertTrue(page.url().contains("inventory"), "Should be on the inventory page after login for user: " + username);
    page.locator(".title").waitFor();
    assertTrue(page.locator(".title").innerText().contains("Products"), "Products title should be visible after login for user: " + username);
  }
}
