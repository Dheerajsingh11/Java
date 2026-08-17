// Pattern  : DECORATOR (Structural)
// Problem  : Add responsibilities to an object at RUN TIME, without subclassing every combination.
// Approach : Wrappers implement the same interface as the thing they wrap, delegate to it, and add
//            behaviour before or after. Real domain: an HTTP client with logging, retry, caching and
//            compression layered on.
// Intuition: Inheritance would need a class per COMBINATION - LoggingClient, RetryingClient,
//            LoggingRetryingClient, LoggingRetryingCachingClient... which explodes as 2^n. Decorators
//            compose instead: each adds one concern, and you stack them in any order at run time.
//            Because a decorator implements the same interface it wraps, the stack is invisible to
//            the caller.
// Time     : O(depth of the stack) per call - one delegation per layer   Space: O(depth)
// Trade-off: Many small classes and a call stack that can be hard to debug (a stack trace shows
//            every layer). ORDER MATTERS and is easy to get wrong - retry-outside-cache behaves very
//            differently from cache-outside-retry. In exchange you get behaviour that can be
//            assembled from configuration rather than fixed at compile time.
// Real use  : java.io streams (new BufferedReader(new InputStreamReader(System.in))),
//            Collections.unmodifiableList(), HttpServletRequestWrapper, Spring's transaction proxies.

import java.util.HashMap;
import java.util.Map;

// The component interface - every decorator implements it too, which is what makes them stackable.
interface HttpFetcher {
    String fetch(String url);
}

// The CONCRETE component: the real work, with no extra concerns mixed in.
class BasicHttpFetcher implements HttpFetcher {
    private int calls = 0;
    public String fetch(String url) {
        calls++;
        if (url.contains("flaky") && calls < 3) {          // fails twice, then succeeds
            throw new RuntimeException("connection reset");
        }
        return "<body of " + url + ">";
    }
}

// The base decorator: holds the wrapped component and delegates. Subclasses override only what they
// need to change, so each concern stays in exactly one small class.
abstract class HttpFetcherDecorator implements HttpFetcher {
    protected final HttpFetcher inner;
    protected HttpFetcherDecorator(HttpFetcher inner) { this.inner = inner; }
    public String fetch(String url) { return inner.fetch(url); }
}

// ---- Concern 1: logging ----
class LoggingFetcher extends HttpFetcherDecorator {
    LoggingFetcher(HttpFetcher inner) { super(inner); }

    @Override
    public String fetch(String url) {
        System.out.println("    [log] -> " + url);
        String result = inner.fetch(url);          // behaviour BEFORE and AFTER the delegation
        System.out.println("    [log] <- " + result.length() + " bytes");
        return result;
    }
}

// ---- Concern 2: retry ----
class RetryingFetcher extends HttpFetcherDecorator {
    private final int attempts;
    RetryingFetcher(HttpFetcher inner, int attempts) { super(inner); this.attempts = attempts; }

    @Override
    public String fetch(String url) {
        RuntimeException last = null;
        for (int i = 1; i <= attempts; i++) {
            try {
                return inner.fetch(url);
            } catch (RuntimeException e) {
                last = e;
                System.out.println("    [retry] attempt " + i + " failed: " + e.getMessage());
            }
        }
        throw last;                                 // all attempts exhausted
    }
}

// ---- Concern 3: caching ----
class CachingFetcher extends HttpFetcherDecorator {
    private final Map<String, String> cache = new HashMap<>();
    CachingFetcher(HttpFetcher inner) { super(inner); }

    @Override
    public String fetch(String url) {
        if (cache.containsKey(url)) {
            System.out.println("    [cache] HIT for " + url);
            return cache.get(url);                  // the inner layers are never called
        }
        System.out.println("    [cache] MISS for " + url);
        String result = inner.fetch(url);
        cache.put(url, result);
        return result;
    }
}

public class DecoratorPattern {
    public static void main(String[] args) {

        // ---- One concern ----
        System.out.println("1. logging only");
        HttpFetcher logged = new LoggingFetcher(new BasicHttpFetcher());
        logged.fetch("https://api.example.com/users");

        // ---- Stacked: cache -> log -> retry -> basic ----
        // Read it OUTSIDE-IN: the cache is consulted first; only on a miss does the call reach the
        // logger, then the retry layer, then the real fetcher.
        System.out.println("2. cache(log(retry(basic))) - stacked at run time");
        HttpFetcher stacked = new CachingFetcher(
                                new LoggingFetcher(
                                  new RetryingFetcher(new BasicHttpFetcher(), 3)));

        System.out.println("  first call to a flaky endpoint:");
        System.out.println("  result = " + stacked.fetch("https://api.example.com/flaky"));

        System.out.println("  second call to the SAME url:");
        System.out.println("  result = " + stacked.fetch("https://api.example.com/flaky"));
        // The retries are gone - the cache short-circuits everything beneath it.

        // ---- ORDER MATTERS ----
        System.out.println("3. why order matters");
        System.out.println("  cache(retry(x)) : a success is cached, so later calls never retry");
        System.out.println("  retry(cache(x)) : each retry re-checks the cache - usually pointless,");
        System.out.println("                    and a cached FAILURE would be retried forever");
    }
}

/* ------------------------- WHY NOT JUST SUBCLASS ------------------------
 * With 4 optional concerns, inheritance needs a class for every combination:
 *     Logging, Retrying, Caching, Compressing,
 *     LoggingRetrying, LoggingCaching, ... LoggingRetryingCachingCompressing
 * That is 2^4 = 16 classes, and 2^n in general. Worse, the combination is fixed at COMPILE time.
 *
 * Decorators need n classes for n concerns, and the combination is chosen at RUN time - which is
 * what lets behaviour come from configuration:
 *     if (config.cacheEnabled()) fetcher = new CachingFetcher(fetcher);
 *
 * ------------------------ DECORATOR vs ITS NEIGHBOURS --------------------
 * DECORATOR keeps the same interface, ADDS behaviour.        (you know you are wrapping)
 * PROXY     keeps the same interface, CONTROLS access.       (lazy loading, permissions)
 * ADAPTER   CHANGES the interface.                           (compatibility)
 * The line between Decorator and Proxy is about intent, not structure - they look almost identical.
 *
 * ------------------------------ WHEN NOT TO USE --------------------------
 * - Only one or two fixed combinations exist - just write those two classes.
 * - The concerns are not independent (each layer must know about the others) - the composition is a
 *   lie and you should design a single component instead.
 * - Deep stacks in hot paths: every layer is an extra virtual call and a longer stack trace.
 * - Java's own java.io is the cautionary tale - powerful, but
 *   `new BufferedReader(new InputStreamReader(new FileInputStream(f)))` is famously verbose.
 * ------------------------------------------------------------------------- */
