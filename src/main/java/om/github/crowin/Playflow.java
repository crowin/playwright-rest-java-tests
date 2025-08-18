package om.github.crowin;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.Cookie;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Playflow — a lightweight Selenide-like static API on top of Playwright.
 *
 * Design goals:
 * - Atomic browser: single Browser instance for the whole JVM (lazy-initialized, thread-safe).
 * - Per-thread isolation: each thread gets its own BrowserContext and Page via ThreadLocal.
 * - Simple, fluent API: open(url), $(selector).click(), $(selector).type("text"), etc.
 * - Small default waits with overridable timeouts using system properties.
 */
public class Playflow {
  private static final AtomicReference<Playwright> PLAYWRIGHT = new AtomicReference<>();
  private static final AtomicReference<Browser> BROWSER = new AtomicReference<>();

  private static final ThreadLocal<BrowserContext> THREAD_CONTEXT = new ThreadLocal<>();
  private static final ThreadLocal<Page> THREAD_PAGE = new ThreadLocal<>();
  private static final AtomicInteger ACTIVE_CONTEXTS = new AtomicInteger(0);


  static {
    Runtime.getRuntime().addShutdownHook(new Thread(Playflow::quit));
  }

  private Playflow() {}

  public static synchronized Page open(String url) {
    var p = page();
    p.navigate(url);
    return p;
  }

  /** Returns the current thread's Page, creating Browser/Context/Page as needed. */
  public static synchronized Page page() {
    ensureBrowser();
    var ctx = THREAD_CONTEXT.get();
    if (ctx == null) {
      var opts = new Browser.NewContextOptions().setLocale(Config.locale());
      var base = Config.baseUrl();
      if (base != null && !base.isEmpty()) {
        opts.setBaseURL(base);
      }
      ctx = BROWSER.get().newContext(opts);
      THREAD_CONTEXT.set(ctx);
      ACTIVE_CONTEXTS.incrementAndGet();
    }
    var p = THREAD_PAGE.get();
    if (p == null || p.isClosed()) {
      p = ctx.newPage();
      THREAD_PAGE.set(p);
    }
    return p;
  }

  public static synchronized void close() {
    var p = THREAD_PAGE.get();
    if (p != null) {
      try { p.close(); } catch (Exception ignored) {}
      THREAD_PAGE.remove();
    }
    var ctx = THREAD_CONTEXT.get();
    if (ctx != null) {
      try { ctx.close(); } catch (Exception ignored) {}
      THREAD_CONTEXT.remove();
      ACTIVE_CONTEXTS.decrementAndGet();
    }
  }

  /**
   * Fully shutdown: closes thread resources and the shared Browser/Playwright.
   */
  public static synchronized void quit() {
    // Always close thread-local resources for this caller
    close();

    // Only tear down shared resources if no other thread has an active context
    if (ACTIVE_CONTEXTS.get() == 0) {
      var b = BROWSER.getAndSet(null);
      if (b != null) {
        try { b.close(); } catch (Exception ignored) {}
      }
      var pw = PLAYWRIGHT.getAndSet(null);
      if (pw != null) {
        try { pw.close(); } catch (Exception ignored) {}
      }
    }
  }

  private static void ensureBrowser() {
    if (BROWSER.get() != null) return;
    synchronized (Playflow.class) {
      if (BROWSER.get() != null) return;
      var pw = PLAYWRIGHT.updateAndGet(existing -> existing != null ? existing : Playwright.create());
      var type = switch (Config.browserType().toLowerCase()) {
          case "firefox" -> pw.firefox();
          case "webkit" -> pw.webkit();
          default -> pw.chromium();
      };
      var browser = type.launch(new BrowserType.LaunchOptions().setHeadless(Config.headless()));
      BROWSER.set(browser);
    }
  }

  public static void initSession(String user, Map<String, String> userCredentials, SessionClient client) {
      Objects.requireNonNull(user, "user");
      // Ensure per-thread context and page are initialized
      page();

      var session = UserSessions.get(user);
      if (session == null || session.isEmpty()) {
          Objects.requireNonNull(userCredentials, "userCredentials");
          Objects.requireNonNull(client, "client");
          try {
              var cookies = client.initSession(userCredentials);
              if (cookies != null && !cookies.isEmpty()) {
                  session = cookies
                          .entrySet()
                          .stream()
                          .map(entry -> new Cookie(entry.getKey(), entry.getValue()).setUrl(Config.baseUrl()))
                          .toList();
                  UserSessions.set(user, session);
              }
          } catch (Exception e) {
              throw new RuntimeException("Failed to initialize session for user: " + user, e);
          }
      }

      if (session != null && !session.isEmpty()) {
          THREAD_CONTEXT.get().addCookies(session);
          THREAD_PAGE.get().reload();
      }
  }

    // ---- Configuration ----
    public static final class Config {
        private Config() {}

        public static String browserType() {
            return System.getProperty("playflow.browser", "chromium");
        }

        public static boolean headless() {
            return Boolean.parseBoolean(System.getProperty("playflow.headless", "false"));
        }

        public static long timeoutMs() {
            return Long.parseLong(System.getProperty("playflow.timeoutMs", "4000"));
        }

        public static long pollingMs() {
            return Long.parseLong(System.getProperty("playflow.pollingMs", "100"));
        }

        public static String locale() {
            return System.getProperty("playflow.locale", "en-US");
        }

        public static String baseUrl() {
            return System.getProperty("playflow.baseUrl", "");
        }
    }
}
