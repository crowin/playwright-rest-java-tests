# Playflow

A lightweight, static API on top of Microsoft Playwright for Java. It provides:

- A single shared Browser instance per JVM (lazy-initialized, thread-safe)
- Thread-local BrowserContext and Page for isolation between tests
- Simple static API: Playflow.open(url), Playflow.page(), Playflow.close(), Playflow.quit()
- Basic configuration via system properties
- Pluggable session initialization through a SessionClient interface (AuthClient implements it)


## Architecture

- Atomic singletons shared across the JVM:
  - Playwright and Browser are stored in AtomicReferences and initialized lazily.
- Per-thread isolation:
  - Each thread gets its own BrowserContext and Page via ThreadLocal.
  - `Playflow.close()` cleans up the current thread’s Page and Context.
- Coordinated shutdown:
  - A shutdown hook calls `Playflow.quit()` to clean up resources.
  - Shared Browser/Playwright are closed only when no active contexts remain.

    
## Configuration

Set via Java system properties:

- `playflow.browser` — chromium | firefox | webkit (default: chromium)
- `playflow.headless` — true | false (default: false)
- `playflow.timeoutMs` — default timeout in ms (default: 4000)
- `playflow.pollingMs` — polling interval in ms (default: 100)
- `playflow.locale` — locale string (default: en-US)
- `playflow.baseUrl` — base URL used to resolve relative paths (default: empty)

Example:

```
-Dplayflow.headless=true -Dplayflow.baseUrl=https://example.com
```


## Basic Usage

Open a URL and work with the current Page:

```java
import com.microsoft.playwright.Page;
import om.github.crowin.Playflow;

Page page = Playflow.open("/login"); // resolves relative to playflow.baseUrl if set
page.locator("#username").fill("standard_user");
page.locator("#password").fill("secret");
page.locator("#login-button").click();

// When done with this thread's page+context
Playflow.close();

// When the whole test suite is done
Playflow.quit();
```


## Sessions with SessionClient and AuthClient

Playflow supports pluggable session initialization through the `SessionClient` interface. This lets you perform an API login and supply cookies to be used later (via a shared storage `UserSessions`).

- `SessionClient` is an interface:

```java
public interface SessionClient {
  /**
   * Implement authentication for the provided user credentials and return cookies to be used in the browser context.
   */
  Map<String, String> initSession(Map<String, String> userCredentials) throws Exception;
}
```

- `AuthClient` is an OkHttp-based HTTP helper that also implements `SessionClient`.
  - `Map<String,String> authenticate(String username, String password)` — performs a POST to your auth endpoint (adjust AuthClient accordingly) and returns cookies as a Map of name -> value.
  - `Map<String,String> initSession(Map<String,String> userCredentials)` — the implementation in AuthClient expects keys "username" and "password" and delegates to `authenticate`.

Example custom SessionClient using AuthClient:

```java
import java.util.Map;

class MySessionClient implements SessionClient {
  private final AuthClient auth = new AuthClient();

  @Override
  public Map<String, String> initSession(Map<String, String> userCredentials) throws Exception {
    String username = userCredentials.get("username");
    String password = userCredentials.get("password");
    return auth.authenticate(username, password);
  }
}
```

Register the session for a user and attach cookies to the current thread's context:

```java
// IMPORTANT: set base URL so Playflow can attach cookies to a domain/url
System.setProperty("playflow.baseUrl", "https://example.com");

Map<String,String> creds = Map.of("username", "standard_user", "password", System.getenv("TEST_PASSWORD"));
Playflow.initSession("standard_user", creds, new MySessionClient());
```

Notes:
- `Playflow.initSession(user, userCredentials, client)` will initialize the per-thread browser context if needed, call your `client.initSession`, convert returned cookies to Playwright cookies bound to `playflow.baseUrl`, add them to the current context, and reload the page.
- Ensure `playflow.baseUrl` is set; otherwise cookie attachment will fail with a clear error.
- Cookies are cached in an internal storage and re-used for the same user within the JVM process.


## Running Tests Locally

- Ensure Playwright browsers are installed. Playwright Java downloads drivers automatically. If needed:

```
./gradlew test
```

- The included tests use a local temporary file (PlayflowTest) and a public demo site (PlayflowRealSiteTest). For CI, you may want to disable the real-site test or set `playflow.headless=true`.


## GitLab CI Example

Below is a minimal `.gitlab-ci.yml` example for running tests headless with Playwright:

```yaml
image: eclipse-temurin:21

variables:
  GRADLE_USER_HOME: "$CI_PROJECT_DIR/.gradle"
  PLAYWRIGHT_BROWSERS_PATH: "$CI_PROJECT_DIR/.pw-browsers"
  JVM_OPTS: "-Xmx2g"

cache:
  key: "$CI_COMMIT_REF_SLUG"
  paths:
    - .gradle/caches/
    - .gradle/wrapper/
    - .pw-browsers/

stages:
  - test

before_script:
  - ./gradlew --version

playflow-tests:
  stage: test
  script:
    - ./gradlew test -Dplayflow.headless=true
  artifacts:
    when: always
    reports:
      junit: build/test-results/test/*.xml
    paths:
      - build/reports/tests/test
```

Notes:
- Using a JDK 21 image here; you can switch to 17 if preferred.
- Playwright Java will download necessary browser binaries automatically to the working directory. The `PLAYWRIGHT_BROWSERS_PATH` helps cache them between CI runs.
- Set `-Dplayflow.headless=true` for CI stability.


## Troubleshooting

- Browser fails to launch in CI:
  - Ensure headless mode is enabled: `-Dplayflow.headless=true`.
  - Confirm the base image has required system dependencies; Playwright bundles most of what it needs for headless runs.

- Sessions not applied:
  - Verify your `SessionClient.initSession` returns a non-empty map of cookie name/value.
  - Ensure `Playflow.initSession(user, client)` is called before opening pages that require authentication.

- Parallel tests interfere:
  - Each thread gets its own BrowserContext and Page. Use `Playflow.close()` after each test to clean up.
  - `Playflow.quit()` should be called once after the whole suite.


## License

MIT (or your preferred license).