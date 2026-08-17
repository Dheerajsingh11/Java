// Pattern  : BUILDER (Creational)
// Problem  : Construct an object that has many optional parameters, without a constructor explosion
//            and without leaving the object temporarily invalid.
// Approach : A separate builder collects values through chained calls, validates once, then produces
//            an immutable object. Real domain: an HTTP request.
// Intuition: With 8 optional fields, constructor overloading needs dozens of signatures and callers
//            end up writing `new Request(url, null, null, 30, null, false, null)` - unreadable and
//            easy to get wrong. Setters fix readability but make the object MUTABLE and allow it to
//            exist half-built. A builder gives named, optional, chainable arguments AND an immutable
//            result: the object is valid the instant it exists.
// Time     : O(number of fields)   Space: O(1) beyond the object
// Trade-off: More code (a whole nested class) and a second object during construction. Worth it past
//            roughly 4 parameters, or when several are optional, or when you want immutability.
//            Below that, a plain constructor - or a `record` - is clearer.
// Real use  : StringBuilder, java.net.http.HttpRequest.newBuilder(), Stream.Builder,
//            Lombok's @Builder, Spring's UriComponentsBuilder, OkHttp's Request.Builder.

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

// The product: fully IMMUTABLE - every field is final and there is no setter.
final class HttpRequest {
    private final String method;                 // required
    private final String url;                    // required
    private final Map<String, String> headers;   // optional
    private final String body;                   // optional
    private final int timeoutSeconds;            // optional, has a default
    private final boolean followRedirects;       // optional, has a default

    // Private: the ONLY way to build one is through the Builder, so validation cannot be skipped.
    private HttpRequest(Builder b) {
        this.method = b.method;
        this.url = b.url;
        this.headers = Collections.unmodifiableMap(new HashMap<>(b.headers)); // defensive copy
        this.body = b.body;
        this.timeoutSeconds = b.timeoutSeconds;
        this.followRedirects = b.followRedirects;
    }

    // Entry point. Required arguments go here, so they cannot be forgotten - the compiler enforces it.
    static Builder newBuilder(String method, String url) {
        return new Builder(method, url);
    }

    static class Builder {
        private final String method;             // required -> final, set in the constructor
        private final String url;
        private Map<String, String> headers = new HashMap<>();
        private String body = null;
        private int timeoutSeconds = 30;         // sensible DEFAULT, so callers need not care
        private boolean followRedirects = true;

        private Builder(String method, String url) {
            this.method = method;
            this.url = url;
        }

        // Each setter returns `this`, which is what makes the calls chainable.
        Builder header(String name, String value) { this.headers.put(name, value); return this; }
        Builder body(String body)                 { this.body = body; return this; }
        Builder timeout(int seconds)              { this.timeoutSeconds = seconds; return this; }
        Builder followRedirects(boolean follow)   { this.followRedirects = follow; return this; }

        // build() is where validation belongs: ONE place, checked before the object exists.
        // With setters on the product itself, there would be no such moment.
        HttpRequest build() {
            if (url == null || url.isBlank())
                throw new IllegalArgumentException("url is required");
            if (!url.startsWith("http"))
                throw new IllegalArgumentException("url must be absolute: " + url);
            if (timeoutSeconds <= 0)
                throw new IllegalArgumentException("timeout must be positive");
            if ("GET".equals(method) && body != null)
                throw new IllegalArgumentException("GET requests cannot have a body");
            return new HttpRequest(this);
        }
    }

    @Override
    public String toString() {
        return method + " " + url
             + "\n    headers=" + headers
             + "\n    body=" + (body == null ? "(none)" : body)
             + "\n    timeout=" + timeoutSeconds + "s, followRedirects=" + followRedirects;
    }
}

public class BuilderPattern {
    public static void main(String[] args) {

        // Minimal: only the required arguments; everything else takes its default.
        HttpRequest simple = HttpRequest.newBuilder("GET", "https://api.example.com/users").build();
        System.out.println("1. minimal\n" + simple);

        // Full: each optional value is NAMED at the call site, so the meaning is obvious.
        // Compare with new HttpRequest("POST", url, map, body, 60, false) - which argument is which?
        HttpRequest full = HttpRequest.newBuilder("POST", "https://api.example.com/orders")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer token123")
                .body("{\"item\":\"book\",\"qty\":2}")
                .timeout(60)
                .followRedirects(false)
                .build();
        System.out.println("2. full\n" + full);

        // Validation happens once, at build() - the object can never exist in an invalid state.
        System.out.println("3. validation");
        try {
            HttpRequest.newBuilder("GET", "https://x.com").body("oops").build();
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }
        try {
            HttpRequest.newBuilder("GET", "not-a-url").build();
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }
    }
}

/* -------------------------- THE PROBLEM BUILDER REPLACES --------------------------
 * TELESCOPING CONSTRUCTORS - one overload per combination:
 *     new Request(url)
 *     new Request(url, headers)
 *     new Request(url, headers, body)
 *     new Request(url, headers, body, timeout)             ... and so on, combinatorially
 * Unreadable at the call site, and you cannot skip a middle parameter.
 *
 * JAVABEANS SETTERS - readable, but two real costs:
 *     Request r = new Request(); r.setUrl(...); r.setBody(...);
 *   1. The object is MUTABLE forever, so it is not thread-safe and not safe as a map key.
 *   2. Between `new` and the last setter it is INVALID - and nothing stops someone using it there.
 *
 * Builder gives the readability of setters with the safety of an immutable, validated object.
 *
 * -------------------------------- WHEN NOT TO USE ----------------------------------
 * - Fewer than ~4 parameters, all required: a plain constructor is clearer.
 * - A simple immutable data carrier: use a `record` (see Java Core/07-OOP/RecordDemo.java).
 * - The object genuinely must be mutable over its lifetime (an entity being edited).
 * ----------------------------------------------------------------------------------- */
