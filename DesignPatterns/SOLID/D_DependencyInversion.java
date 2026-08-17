// Principle : DEPENDENCY INVERSION (the D in SOLID)
// Statement : High-level modules should not depend on low-level modules. Both should depend on
//             ABSTRACTIONS. Abstractions should not depend on details; details depend on abstractions.
// Problem   : Business logic that constructs its own database, mailer and payment client is welded
//             to them - untestable, unswappable, and it recompiles whenever they change.
// Intuition : The word "inversion" refers to the direction of the arrow. Normally the important code
//             points DOWN at the plumbing. Invert it: define the interface where the BUSINESS LOGIC
//             lives, and make the plumbing implement it. Now both point at the abstraction, and the
//             business rules no longer know a database exists.
// Benefit   : Swap implementations freely, and - the practical payoff - test the business logic with
//             no database, no network and no email server.
// Trade-off : More interfaces, and the wiring must happen somewhere (main, or a DI container).
//             Creating an interface with exactly ONE implementation that will never change is
//             ceremony; do it where substitution or testing genuinely matters.

import java.util.ArrayList;
import java.util.List;

// ============================================================================
// BEFORE - high-level logic constructing its own low-level dependencies
// ============================================================================
class MySqlDatabase {
    void insertOrder(String id, double amount) {
        System.out.println("      MySQL: INSERT order " + id + " amount " + amount);
    }
}

class SmtpMailer {
    void send(String to, String body) { System.out.println("      SMTP: -> " + to + " : " + body); }
}

class OrderServiceBefore {
    // The dependencies are created INSIDE. That is the violation - this class is now permanently
    // bound to MySQL and SMTP.
    private final MySqlDatabase database = new MySqlDatabase();
    private final SmtpMailer mailer = new SmtpMailer();

    void placeOrder(String id, double amount, String customerEmail) {
        database.insertOrder(id, amount);
        mailer.send(customerEmail, "Order " + id + " confirmed");
    }

    // Consequences:
    //  - Cannot unit-test: every test hits a real database and sends real email.
    //  - Cannot switch to PostgreSQL without editing this business class.
    //  - Cannot reuse the logic in a context with different infrastructure.
}

// ============================================================================
// AFTER - both sides depend on abstractions
// ============================================================================

// The interfaces are defined in terms of the BUSINESS need, not the technology. Note the naming:
// "OrderRepository", not "Database" - the business does not care that storage exists.
interface OrderRepository { void save(String id, double amount); }
interface NotificationSender { void notifyCustomer(String email, String message); }

// ---- Low-level DETAILS now depend on the abstraction (the arrow is inverted) ----
class MySqlOrderRepository implements OrderRepository {
    public void save(String id, double amount) {
        System.out.println("      MySQL: INSERT order " + id + " amount " + amount);
    }
}

class PostgresOrderRepository implements OrderRepository {
    public void save(String id, double amount) {
        System.out.println("      Postgres: INSERT order " + id + " amount " + amount);
    }
}

class SmtpNotificationSender implements NotificationSender {
    public void notifyCustomer(String email, String message) {
        System.out.println("      SMTP: -> " + email + " : " + message);
    }
}

class SmsNotificationSender implements NotificationSender {
    public void notifyCustomer(String phone, String message) {
        System.out.println("      SMS: -> " + phone + " : " + message);
    }
}

// ---- Test doubles: possible ONLY because of the inversion ----
class InMemoryOrderRepository implements OrderRepository {
    final List<String> saved = new ArrayList<>();
    public void save(String id, double amount) { saved.add(id + ":" + amount); }
}

class RecordingNotificationSender implements NotificationSender {
    final List<String> sent = new ArrayList<>();
    public void notifyCustomer(String to, String message) { sent.add(to + " | " + message); }
}

// ---- The HIGH-LEVEL policy: depends only on interfaces ----
class OrderService {
    private final OrderRepository repository;
    private final NotificationSender notifier;

    // CONSTRUCTOR INJECTION: dependencies are handed in, not constructed. This is what makes the
    // class testable and reusable. It also makes the dependencies VISIBLE in the signature, rather
    // than hidden inside the body.
    OrderService(OrderRepository repository, NotificationSender notifier) {
        this.repository = repository;
        this.notifier = notifier;
    }

    void placeOrder(String id, double amount, String contact) {
        // Business rule - the only thing this class should actually own.
        if (amount <= 0) throw new IllegalArgumentException("amount must be positive");

        repository.save(id, amount);
        notifier.notifyCustomer(contact, "Order " + id + " confirmed");
    }
}

public class D_DependencyInversion {
    public static void main(String[] args) {

        System.out.println("BEFORE - hard-wired to MySQL and SMTP:");
        new OrderServiceBefore().placeOrder("ORD-1", 500, "asha@example.com");
        System.out.println("      testing this would require a real database and mail server.");

        System.out.println("AFTER - the same logic, infrastructure chosen from outside:");
        OrderService withMySql = new OrderService(new MySqlOrderRepository(), new SmtpNotificationSender());
        withMySql.placeOrder("ORD-2", 750, "bala@example.com");

        System.out.println("    swapping the database and the channel changes NO business code:");
        OrderService withPostgres = new OrderService(new PostgresOrderRepository(), new SmsNotificationSender());
        withPostgres.placeOrder("ORD-3", 1200, "+91-90000-11111");

        System.out.println("THE REAL PAYOFF - a unit test with no infrastructure at all:");
        InMemoryOrderRepository fakeRepo = new InMemoryOrderRepository();
        RecordingNotificationSender fakeNotifier = new RecordingNotificationSender();
        OrderService underTest = new OrderService(fakeRepo, fakeNotifier);

        underTest.placeOrder("ORD-4", 300, "cara@example.com");
        System.out.println("      repository received : " + fakeRepo.saved);
        System.out.println("      notification sent   : " + fakeNotifier.sent);

        System.out.println("    and the business rule can be tested directly:");
        try {
            underTest.placeOrder("ORD-5", -10, "x@y.com");
        } catch (IllegalArgumentException e) {
            System.out.println("      rejected: " + e.getMessage() + "  (no DB or SMTP involved)");
        }
    }
}

/* ------------------------ WHAT "INVERSION" ACTUALLY MEANS ------------------------
 * TRADITIONAL - the arrow points downwards:
 *       OrderService  ---->  MySqlDatabase
 *       (policy)             (detail)
 *   The important code depends on the plumbing.
 *
 * INVERTED - both point at the abstraction:
 *       OrderService  ---->  OrderRepository  <----  MySqlOrderRepository
 *       (policy)             (abstraction)           (detail)
 *   The plumbing now depends on an interface expressed in the BUSINESS's language. The direction of
 *   the source-code dependency has been reversed relative to the direction of control flow - hence
 *   "inversion".
 *
 * Naming matters here: calling the interface `OrderRepository` rather than `Database` keeps it
 * owned by the business layer. An interface named after the technology has not really inverted
 * anything.
 *
 * ---------------------------- DIP vs DI vs IoC ----------------------------------
 * These three get conflated constantly:
 *   DIP (this file)         - a DESIGN principle: depend on abstractions.
 *   Dependency Injection    - a TECHNIQUE for supplying them (constructor, setter, or field).
 *   Inversion of Control    - the broader idea that a framework calls YOUR code, not the reverse.
 * Spring provides DI as a container; DIP is what makes that useful. You can follow DIP with no
 * framework at all, as `main` does above - that wiring point is sometimes called the composition root.
 *
 * ------------------------------ WHICH INJECTION ---------------------------------
 * CONSTRUCTOR (used here) - dependencies are required, visible in the signature, and the object is
 *   never in a half-built state. Fields can be `final`. This is the default choice.
 * SETTER - for genuinely optional dependencies; leaves the object temporarily incomplete.
 * FIELD (@Autowired on a private field) - concise but hides dependencies and cannot be set in a
 *   plain unit test without reflection. Widely discouraged.
 *
 * -------------------------------- WHEN NOT TO USE --------------------------------
 * - Stable library types you would never substitute: String, LocalDate, Math. Injecting them is noise.
 * - A single implementation that genuinely will not change and needs no test double.
 * - Small scripts, where the wiring costs more than it saves.
 * ---------------------------------------------------------------------------------- */
