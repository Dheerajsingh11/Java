// Pattern  : SINGLETON (Creational)
// Problem  : Guarantee that exactly ONE instance of a class exists, with a global access point.
// Approach : Four implementations shown - eager, holder idiom, double-checked locking, and enum -
//            with the thread-safety and serialization trade-offs of each.
// Intuition: Some resources genuinely must be shared and unique: a connection pool, a configuration
//            registry, a logger. Creating a second one would either waste a scarce resource or split
//            state that must stay consistent. Singleton makes "there is only one" a compile-and-run
//            guarantee rather than a convention everyone has to remember.
// Time     : instance access O(1)   Space: O(1)
// Trade-off: THE MOST OVERUSED PATTERN. It is global mutable state wearing a design-pattern costume:
//            it hides dependencies (a class using it does not declare it), makes unit testing hard
//            (tests leak state into each other), and is a bottleneck under concurrency. Reach for
//            dependency injection first; use Singleton only for genuinely single, stateless-or-
//            carefully-guarded resources.
// Real use  : java.lang.Runtime, Spring beans (singleton scope by default), connection pools,
//            logger factories, configuration holders.

import java.util.HashMap;
import java.util.Map;

// ---------------------------------------------------------------------------
// 1. EAGER - created when the class loads.
//    Simple and thread-safe (the JVM guarantees class initialization is atomic), but the instance
//    is built even if it is never used. Fine for cheap objects, wasteful for expensive ones.
// ---------------------------------------------------------------------------
class EagerConfig {
    private static final EagerConfig INSTANCE = new EagerConfig();
    private final Map<String, String> settings = new HashMap<>();

    private EagerConfig() {                     // private constructor - nobody else can call new
        settings.put("env", "production");
    }

    static EagerConfig getInstance() { return INSTANCE; }
    String get(String key) { return settings.get(key); }
}

// ---------------------------------------------------------------------------
// 2. HOLDER IDIOM - lazy AND thread-safe, with no synchronization cost.
//    The nested class is not loaded until getInstance() first touches it, and the JVM's class
//    loading is itself thread-safe. This is the best non-enum choice.
// ---------------------------------------------------------------------------
class LazyConnectionPool {
    private LazyConnectionPool() {
        System.out.println("  (connection pool created - expensive, so only on first use)");
    }

    private static class Holder {               // loaded only when Holder is first referenced
        private static final LazyConnectionPool INSTANCE = new LazyConnectionPool();
    }

    static LazyConnectionPool getInstance() { return Holder.INSTANCE; }
    String borrow() { return "connection#1"; }
}

// ---------------------------------------------------------------------------
// 3. DOUBLE-CHECKED LOCKING - lazy, thread-safe, shown because it is asked about constantly.
//    The `volatile` is NOT optional: without it, another thread can observe a non-null reference
//    to a partially constructed object, because construction and assignment may be reordered.
// ---------------------------------------------------------------------------
class DclLogger {
    private static volatile DclLogger instance;   // volatile is load-bearing here
    private DclLogger() { }

    static DclLogger getInstance() {
        if (instance == null) {                   // check 1: unsynchronized fast path
            synchronized (DclLogger.class) {
                if (instance == null) {           // check 2: only one thread may construct
                    instance = new DclLogger();
                }
            }
        }
        return instance;
    }
    void log(String msg) { System.out.println("  [LOG] " + msg); }
}

// ---------------------------------------------------------------------------
// 4. ENUM - the recommended form (Effective Java, Item 3).
//    The JVM guarantees a single instance, and it is free from the two attacks the others need
//    extra code to survive: REFLECTION (Constructor.setAccessible cannot instantiate an enum) and
//    SERIALIZATION (enums deserialize to the same constant, other singletons create a new object).
// ---------------------------------------------------------------------------
enum AppRegistry {
    INSTANCE;                                     // the single instance

    private final Map<String, String> services = new HashMap<>();

    AppRegistry() { services.put("payments", "https://payments.internal"); }

    void register(String name, String url) { services.put(name, url); }
    String lookup(String name) { return services.get(name); }
}

public class SingletonPattern {
    public static void main(String[] args) {
        System.out.println("1. EAGER");
        System.out.println("  env = " + EagerConfig.getInstance().get("env"));
        System.out.println("  same instance? " + (EagerConfig.getInstance() == EagerConfig.getInstance()));

        System.out.println("2. HOLDER (lazy)");
        System.out.println("  before first call - nothing constructed yet");
        System.out.println("  borrowed " + LazyConnectionPool.getInstance().borrow());
        System.out.println("  second call reuses it: "
                + (LazyConnectionPool.getInstance() == LazyConnectionPool.getInstance()));

        System.out.println("3. DOUBLE-CHECKED LOCKING");
        DclLogger.getInstance().log("started");

        System.out.println("4. ENUM (recommended)");
        AppRegistry.INSTANCE.register("orders", "https://orders.internal");
        System.out.println("  payments -> " + AppRegistry.INSTANCE.lookup("payments"));
        System.out.println("  orders   -> " + AppRegistry.INSTANCE.lookup("orders"));
        System.out.println("  same instance? " + (AppRegistry.INSTANCE == AppRegistry.valueOf("INSTANCE")));
    }
}

/* ------------------------------ CHOOSING A FORM ------------------------------
 * | Form      | Lazy | Thread-safe | Reflection-safe | Serialization-safe | Verdict            |
 * | Eager     | no   | yes         | no              | no                 | fine if cheap      |
 * | Holder    | YES  | yes         | no              | no                 | best non-enum      |
 * | DCL       | yes  | yes (volatile!) | no          | no                 | rarely needed now  |
 * | Enum      | no   | yes         | YES             | YES                | RECOMMENDED        |
 *
 * -------------------------------- WHEN NOT TO USE -----------------------------
 * - You need more than one instance later (testing, multi-tenancy) - singletons are hard to undo.
 * - The class holds MUTABLE state shared across threads - now every user is a concurrency risk.
 * - You are using it to avoid passing a parameter. That is a hidden dependency, not a design.
 * - In a DI framework (Spring), let the container manage lifecycle instead - you get single
 *   instances without the global static coupling, and can swap them in tests.
 *
 * TESTABILITY is the usual killer: a static singleton cannot be replaced with a fake, so any test
 * touching it exercises the real thing and leaks state into the next test.
 * ------------------------------------------------------------------------------ */
