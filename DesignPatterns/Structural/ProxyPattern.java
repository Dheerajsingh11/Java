// Pattern  : PROXY (Structural)
// Problem  : Control access to an object - defer its creation, check permissions, cache, or log -
//            without the caller knowing.
// Approach : A stand-in implements the same interface as the real object and decides what to do
//            before (or instead of) delegating. Three kinds shown: virtual, protection, and logging.
// Intuition: Sometimes the object itself is fine but ACCESS to it needs managing: it is expensive to
//            create, or remote, or restricted. A proxy sits in front with an identical interface, so
//            callers are unaware anything is between them and the real thing.
// Time     : O(1) delegation; a virtual proxy converts eager cost into deferred cost
// Space    : O(1) per proxy
// Trade-off: An extra indirection and a class that must faithfully mirror the real one's contract.
//            A proxy that changes observable behaviour (swallows exceptions, returns stale data)
//            breaks the substitutability the pattern depends on. Real frameworks generate proxies
//            dynamically to avoid the boilerplate.
// Real use  : Spring @Transactional and @Cacheable (AOP proxies), Hibernate lazy-loaded entities,
//            java.lang.reflect.Proxy, RMI stubs, mock objects in tests.

import java.util.HashMap;
import java.util.Map;

// The shared interface. Real object and proxies are indistinguishable to callers.
interface ReportService {
    String generate(String reportName);
}

// The REAL subject: expensive to construct and slow to run.
class RealReportService implements ReportService {
    RealReportService() {
        System.out.println("    (RealReportService constructed - loads 200MB of data)");
        sleep(50);
    }
    public String generate(String reportName) {
        sleep(30);                                    // pretend this is a heavy query
        return "REPORT[" + reportName + "]";
    }
    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}

// ---------------------------------------------------------------------------
// 1. VIRTUAL PROXY - defers creating the expensive object until it is genuinely needed.
//    If the caller never asks for a report, the 200MB is never loaded.
// ---------------------------------------------------------------------------
class LazyReportProxy implements ReportService {
    private RealReportService real;                   // stays null until first use

    public String generate(String reportName) {
        if (real == null) {                           // create ON DEMAND, not up front
            System.out.println("    [virtual] first use - constructing the real service now");
            real = new RealReportService();
        }
        return real.generate(reportName);
    }
}

// ---------------------------------------------------------------------------
// 2. PROTECTION PROXY - enforces authorization before delegating.
//    The real service stays free of permission logic, which keeps it single-purpose.
// ---------------------------------------------------------------------------
class SecuredReportProxy implements ReportService {
    private final ReportService inner;
    private final String role;

    SecuredReportProxy(ReportService inner, String role) { this.inner = inner; this.role = role; }

    public String generate(String reportName) {
        // "financial" reports require elevated rights; everything else is open.
        if (reportName.startsWith("financial") && !"ADMIN".equals(role)) {
            throw new SecurityException("role " + role + " may not read " + reportName);
        }
        return inner.generate(reportName);
    }
}

// ---------------------------------------------------------------------------
// 3. CACHING PROXY - avoids repeating expensive work.
//    (Structurally identical to a caching DECORATOR - the difference is intent: a proxy CONTROLS
//     access to the subject, a decorator ADDS behaviour to it.)
// ---------------------------------------------------------------------------
class CachingReportProxy implements ReportService {
    private final ReportService inner;
    private final Map<String, String> cache = new HashMap<>();

    CachingReportProxy(ReportService inner) { this.inner = inner; }

    public String generate(String reportName) {
        return cache.computeIfAbsent(reportName, name -> {
            System.out.println("    [cache] miss - delegating for " + name);
            return inner.generate(name);
        });
    }
}

public class ProxyPattern {
    public static void main(String[] args) {

        System.out.println("1. VIRTUAL proxy - nothing is constructed until first use");
        ReportService lazy = new LazyReportProxy();
        System.out.println("  proxy created, real service NOT built yet");
        System.out.println("  " + lazy.generate("sales-q3"));
        System.out.println("  " + lazy.generate("sales-q4") + "   (reuses the same instance)");

        System.out.println("2. PROTECTION proxy");
        ReportService asUser  = new SecuredReportProxy(new LazyReportProxy(), "USER");
        ReportService asAdmin = new SecuredReportProxy(new LazyReportProxy(), "ADMIN");
        System.out.println("  USER  reads public   : " + asUser.generate("headcount"));
        try {
            asUser.generate("financial-2026");
        } catch (SecurityException e) {
            System.out.println("  USER  reads financial: BLOCKED - " + e.getMessage());
        }
        System.out.println("  ADMIN reads financial: " + asAdmin.generate("financial-2026"));

        System.out.println("3. CACHING proxy - second call skips the work");
        ReportService cached = new CachingReportProxy(new LazyReportProxy());
        long t0 = System.currentTimeMillis();
        cached.generate("inventory");
        long firstCall = System.currentTimeMillis() - t0;
        t0 = System.currentTimeMillis();
        cached.generate("inventory");
        long secondCall = System.currentTimeMillis() - t0;
        System.out.println("  first call: " + firstCall + "ms, second call: " + secondCall + "ms");

        System.out.println("4. proxies COMPOSE - security outside caching outside lazy loading");
        ReportService full = new SecuredReportProxy(
                                new CachingReportProxy(new LazyReportProxy()), "ADMIN");
        System.out.println("  " + full.generate("financial-2026"));
    }
}

/* --------------------------------- KINDS OF PROXY ---------------------------------
 * VIRTUAL     defer creating an expensive object until needed   (Hibernate lazy loading)
 * PROTECTION  enforce access rules before delegating            (Spring Security)
 * REMOTE      hide that the object lives on another machine     (RMI, gRPC stubs)
 * CACHING     serve stored results instead of recomputing       (Spring @Cacheable)
 * LOGGING     record calls for audit or debugging               (AOP interceptors)
 * SMART       add reference counting, locking, or lifecycle work
 *
 * ------------------------------ PROXY vs DECORATOR --------------------------------
 * They are structurally near-identical; the difference is INTENT and who is in charge:
 *   DECORATOR - the caller deliberately builds a stack to ADD behaviour.
 *   PROXY     - the caller usually does not know a proxy exists; it CONTROLS access to the subject
 *               and often decides whether the subject is called at all.
 * A caching proxy may skip the real object entirely. A decorator always delegates.
 *
 * ------------------------------- WHEN NOT TO USE ----------------------------------
 * - The object is cheap to create - a virtual proxy adds complexity for nothing.
 * - You want to CHANGE the interface -> that is an Adapter.
 * - Authorization belongs in one place at the system boundary rather than scattered across proxies.
 * - Hand-written proxies for many methods are pure boilerplate; use java.lang.reflect.Proxy or a
 *   framework's AOP support instead.
 * - Beware proxies that hide COST: a lazy proxy that quietly triggers a database load inside a loop
 *   is the classic Hibernate N+1 problem.
 * ----------------------------------------------------------------------------------- */
