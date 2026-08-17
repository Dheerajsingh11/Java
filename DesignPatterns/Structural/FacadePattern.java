// Pattern  : FACADE (Structural)
// Problem  : Give clients ONE simple entry point to a complicated subsystem of many collaborating classes.
// Approach : A facade exposes a small, task-oriented API and orchestrates the subsystem behind it.
//            Real domain: placing an order (inventory + payment + shipping + notification + audit).
// Intuition: A subsystem is complex because it must be - it handles many cases. But 90% of callers
//            want one common workflow. Forcing every caller to learn the call order, the rollback
//            rules and the failure handling duplicates that knowledge everywhere and guarantees it
//            will be got wrong somewhere. A facade captures the workflow ONCE.
// Time     : O(work of the underlying calls)   Space: O(1)
// Trade-off: The facade can become a "god object" if it grows to expose everything - at that point
//            it is just the subsystem with extra steps. Keep it TASK-oriented, and leave the
//            subsystem classes public so advanced callers can still bypass it when they need to.
// Real use  : javax.faces.context.FacesContext, SLF4J over logging backends, Spring's JdbcTemplate
//            over raw JDBC, java.net.URL.openStream() over the connection machinery.

// ---------------------- the subsystem: several focused classes ----------------------
class InventoryService {
    boolean isInStock(String sku, int qty) { return !sku.equals("SOLD-OUT") && qty <= 5; }
    String reserve(String sku, int qty)    { return "RES-" + sku + "-" + qty; }
    void release(String reservationId)     { System.out.println("      inventory: released " + reservationId); }
}

class PricingService {
    double priceFor(String sku, int qty) { return 199.0 * qty; }
    double taxFor(double amount)         { return amount * 0.18; }
}

class PaymentService {
    String charge(String customer, double amount) {
        if (amount > 5000) throw new IllegalStateException("card limit exceeded");
        return "PAY-" + customer.hashCode();
    }
    void refund(String paymentId) { System.out.println("      payment: refunded " + paymentId); }
}

class ShippingService {
    String schedule(String address, String reservationId) { return "SHIP-" + reservationId; }
}

class NotificationService {
    void orderConfirmed(String customer, String shipmentId) {
        System.out.println("      notify: emailed " + customer + " about " + shipmentId);
    }
}

class AuditLog {
    void record(String event) { System.out.println("      audit: " + event); }
}

// ------------------------------------ THE FACADE ------------------------------------
class OrderFacade {
    // The facade OWNS the subsystem objects. Callers never see them.
    private final InventoryService inventory = new InventoryService();
    private final PricingService pricing = new PricingService();
    private final PaymentService payments = new PaymentService();
    private final ShippingService shipping = new ShippingService();
    private final NotificationService notifications = new NotificationService();
    private final AuditLog audit = new AuditLog();

    // ONE method for the common task. All the sequencing, the failure handling and the compensating
    // rollback live here - written once, correct once.
    String placeOrder(String customer, String sku, int qty, String address) {
        audit.record("order attempt " + sku + " x" + qty);

        if (!inventory.isInStock(sku, qty)) {
            audit.record("rejected - out of stock");
            throw new IllegalStateException("out of stock: " + sku);
        }

        String reservation = inventory.reserve(sku, qty);
        String payment = null;
        try {
            double subtotal = pricing.priceFor(sku, qty);
            double total = subtotal + pricing.taxFor(subtotal);
            payment = payments.charge(customer, total);

            String shipment = shipping.schedule(address, reservation);
            notifications.orderConfirmed(customer, shipment);
            audit.record("order completed " + shipment + " total " + total);
            return shipment;

        } catch (RuntimeException e) {
            // COMPENSATION on failure - the part callers would most often forget or get wrong.
            // Release the stock, refund if we already charged, and re-throw.
            inventory.release(reservation);
            if (payment != null) payments.refund(payment);
            audit.record("order failed and rolled back: " + e.getMessage());
            throw e;
        }
    }
}

public class FacadePattern {
    public static void main(String[] args) {

        OrderFacade orders = new OrderFacade();

        System.out.println("1. successful order - ONE call from the client");
        String shipment = orders.placeOrder("asha@example.com", "BOOK-42", 2, "12 Main St");
        System.out.println("  -> " + shipment);

        System.out.println("2. out of stock - rejected before anything is reserved");
        try {
            orders.placeOrder("bala@example.com", "SOLD-OUT", 1, "9 Park Rd");
        } catch (IllegalStateException e) {
            System.out.println("  -> " + e.getMessage());
        }

        System.out.println("3. payment fails - the facade ROLLS BACK the reservation");
        try {
            orders.placeOrder("cara@example.com", "BOOK-42", 5, "44 Hill Ave");   // total > 5000
        } catch (IllegalStateException e) {
            System.out.println("  -> " + e.getMessage());
        }

        System.out.println();
        System.out.println("Without the facade, EVERY caller would have to know:");
        System.out.println("  check stock -> reserve -> price -> tax -> charge -> ship -> notify -> audit");
        System.out.println("  ...and how to unwind each step correctly when something fails.");
    }
}

/* --------------------------- WHAT THE FACADE ACTUALLY BUYS ---------------------------
 * 1. ONE place to get the workflow right. The rollback logic above is easy to forget; centralizing
 *    it means it cannot be forgotten in the fifth call site.
 * 2. DECOUPLING. Callers depend on OrderFacade, not on six services. Swapping the payment provider
 *    changes one file rather than every caller.
 * 3. A smaller surface to learn. New developers call placeOrder() rather than reading six classes.
 *
 * ------------------------------- IMPORTANT NON-GOAL ----------------------------------
 * A facade does NOT hide or forbid the subsystem. The services remain usable directly for the rare
 * caller with an unusual need - a refund tool might use PaymentService alone. A facade that LOCKS
 * the subsystem away forces every future edge case through it, and that is how it grows into a god
 * object.
 *
 * ---------------------------- FACADE vs ITS NEIGHBOURS -------------------------------
 * FACADE    SIMPLIFIES a subsystem of many classes.       (new, smaller interface)
 * ADAPTER   CHANGES one class's interface to fit.          (compatibility)
 * MEDIATOR  centralizes how peers TALK TO EACH OTHER.      (peers know the mediator, not each other)
 * A facade is one-directional: clients call it, the subsystem does not call back.
 *
 * -------------------------------- WHEN NOT TO USE ------------------------------------
 * - The subsystem is one or two classes already - the facade adds a layer for nothing.
 * - Callers genuinely need fine-grained control; a facade would force awkward workarounds.
 * - The facade starts growing a method per subsystem method - it has become a pass-through, and the
 *   simplification it was meant to provide is gone.
 * -------------------------------------------------------------------------------------- */
