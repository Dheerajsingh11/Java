// Pattern  : OBSERVER (Behavioural)
// Problem  : Notify many interested parties when something happens, without the source knowing who
//            they are.
// Approach : A subject keeps a list of observers and broadcasts events to them. Real domain: an
//            order whose status change must trigger email, inventory, analytics and audit.
// Intuition: When an order ships, several unrelated things must happen. If the order code calls each
//            one directly it becomes coupled to all of them - adding SMS notifications means editing
//            the order class, and testing it now requires stubbing four services. Observer inverts
//            this: the order announces "I shipped" and does not care who listens.
// Time     : O(n) per event, n = observers   Space: O(n)
// Trade-off: The flow becomes harder to FOLLOW - reading the subject tells you nothing about what
//            actually happens, and debugging means finding every registered listener. Also watch for
//            memory leaks (observers that are never unsubscribed) and for one slow or throwing
//            observer disrupting the others.
// Real use  : Swing/JavaFX listeners, java.beans.PropertyChangeListener, Spring ApplicationEvent,
//            Kafka consumers, RxJava/Reactive Streams, the DOM's addEventListener.

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// ---- The event: immutable, carrying everything a listener might need ----
record OrderEvent(String orderId, String status, double amount) { }

// ---- The observer interface ----
interface OrderListener {
    void onOrderEvent(OrderEvent event);
    default String listenerName() { return getClass().getSimpleName(); }
}

// ---- Concrete observers: each does ONE thing and knows nothing about the others ----
class EmailNotifier implements OrderListener {
    public void onOrderEvent(OrderEvent e) {
        System.out.println("      [email]     order " + e.orderId() + " is now " + e.status());
    }
}

class InventoryUpdater implements OrderListener {
    public void onOrderEvent(OrderEvent e) {
        if ("SHIPPED".equals(e.status())) {
            System.out.println("      [inventory] decrementing stock for " + e.orderId());
        }
        // Observers may IGNORE events they do not care about - that is normal and expected.
    }
}

class AnalyticsCollector implements OrderListener {
    private double revenue = 0;
    public void onOrderEvent(OrderEvent e) {
        if ("PAID".equals(e.status())) revenue += e.amount();
        System.out.println("      [analytics] revenue so far " + revenue);
    }
}

// An observer that FAILS - to show why the subject must be defensive.
class FlakyAuditLogger implements OrderListener {
    public void onOrderEvent(OrderEvent e) {
        throw new RuntimeException("audit service unreachable");
    }
}

// ---- THE SUBJECT ----
class OrderService {
    // LinkedHashMap keeps registration order predictable and allows removal by key.
    private final Map<String, OrderListener> listeners = new LinkedHashMap<>();

    void subscribe(OrderListener listener) { listeners.put(listener.listenerName(), listener); }

    // UNSUBSCRIBE matters: without it, listeners are retained forever and leak memory. This is the
    // single most common bug with this pattern.
    void unsubscribe(String name) { listeners.remove(name); }

    void updateStatus(String orderId, String status, double amount) {
        System.out.println("  order " + orderId + " -> " + status);
        OrderEvent event = new OrderEvent(orderId, status, amount);

        // Iterate over a COPY: a listener that unsubscribes itself during notification would
        // otherwise cause ConcurrentModificationException.
        List<OrderListener> snapshot = new ArrayList<>(listeners.values());

        for (OrderListener listener : snapshot) {
            try {
                listener.onOrderEvent(event);
            } catch (RuntimeException ex) {
                // ISOLATE FAILURES. One broken observer must not prevent the others from running,
                // nor break the subject. Log and carry on.
                System.out.println("      [!] " + listener.listenerName() + " failed: " + ex.getMessage());
            }
        }
    }
}

public class ObserverPattern {
    public static void main(String[] args) {

        OrderService orders = new OrderService();
        orders.subscribe(new EmailNotifier());
        orders.subscribe(new InventoryUpdater());
        orders.subscribe(new AnalyticsCollector());

        System.out.println("Three listeners registered:");
        orders.updateStatus("ORD-1", "PAID", 1500);
        orders.updateStatus("ORD-1", "SHIPPED", 1500);

        System.out.println("Adding a listener at run time - the subject needs no change:");
        orders.subscribe(new OrderListener() {
            public void onOrderEvent(OrderEvent e) {
                System.out.println("      [sms]       texting customer about " + e.orderId());
            }
            public String listenerName() { return "SmsNotifier"; }
        });
        orders.updateStatus("ORD-2", "PAID", 800);

        System.out.println("A failing listener must not break the others:");
        orders.subscribe(new FlakyAuditLogger());
        orders.updateStatus("ORD-3", "PAID", 2000);

        System.out.println("Unsubscribing (this is what prevents memory leaks):");
        orders.unsubscribe("FlakyAuditLogger");
        orders.unsubscribe("SmsNotifier");
        orders.updateStatus("ORD-4", "SHIPPED", 950);
    }
}

/* ----------------------------- WHAT IT DECOUPLES -----------------------------
 * Without Observer, OrderService must call every collaborator itself:
 *
 *     void updateStatus(...) {
 *         emailService.send(...);
 *         inventoryService.decrement(...);
 *         analytics.record(...);
 *         auditLog.write(...);          // and edit this method for every new concern
 *     }
 *
 * That class now depends on four services, cannot be unit-tested without stubbing all of them, and
 * must be modified for every new reaction. With Observer it depends on ONE interface and never
 * changes again.
 *
 * ------------------------------- THE REAL PITFALLS ----------------------------
 * 1. MEMORY LEAKS - the classic. A subject holds strong references to its observers, so a listener
 *    that is never unsubscribed keeps its whole object graph alive. In long-lived applications this
 *    is a leading cause of heap growth. Always provide, and call, unsubscribe.
 * 2. ORDERING - observers should not depend on running in a particular order. If they do, they are
 *    not independent and the pattern is being misused.
 * 3. EXCEPTIONS - one throwing observer can abort the notification loop. Isolate each call, as above.
 * 4. RE-ENTRANCY - an observer that triggers another event can cause infinite recursion.
 * 5. DEBUGGING - reading the subject tells you nothing about what happens. This is the real cost of
 *    the decoupling and is worth accepting knowingly.
 *
 * ------------------------------ SYNC vs ASYNC --------------------------------
 * This implementation is SYNCHRONOUS: a slow observer blocks the subject. For anything heavy,
 * dispatch to an executor or a message queue instead - which is exactly what Kafka and Spring's
 * @Async event listeners provide.
 *
 * NOTE: java.util.Observer/Observable were DEPRECATED in Java 9 - they were not thread-safe, could
 * not be serialized usefully, and forced inheritance. Use your own interface (as here),
 * PropertyChangeListener, or a reactive library.
 * ------------------------------------------------------------------------------ */
