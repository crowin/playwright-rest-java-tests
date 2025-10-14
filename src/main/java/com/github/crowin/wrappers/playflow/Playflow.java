package com.github.crowin.wrappers.playflow;

import com.microsoft.playwright.*;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Playflow — a lightweight Selenide-like static API on top of Playwright.
 */
public class Playflow {
    private static final AtomicReference<Playwright> PLAYWRIGHT = new AtomicReference<>();
    private static final AtomicReference<Browser> BROWSER = new AtomicReference<>();

    static final ThreadLocal<BrowserContext> THREAD_CONTEXT = new ThreadLocal<>();
    static final ThreadLocal<Page> THREAD_PAGE = new ThreadLocal<>();
    private static final AtomicInteger ACTIVE_CONTEXTS = new AtomicInteger(0);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Playflow::quit));
    }

    private Playflow() {}

    @SuppressWarnings("resource")
    public static synchronized Page open(String url) {
        var p = page();
        p.navigate(url);
        return p;
    }

    @SuppressWarnings("resource")
    public static synchronized Page open() {
        return open("");
    }

    /** Returns the current thread's Page, creating Browser/Context/Page as needed. */
    @SuppressWarnings("resource")
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

    @SuppressWarnings("resource")
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

    public static Extensions extensions() {
        return new Extensions();
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
            PLAYWRIGHT.get().selectors().setTestIdAttribute(Config.defaultTestId());
            var browser = type.launch(new BrowserType.LaunchOptions().setHeadless(Config.headless()));
            BROWSER.set(browser);
        }
    }
}
