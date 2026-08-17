// Pattern  : STATE (Behavioural)
// Problem  : An object must behave differently depending on its internal state, and the transitions
//            between states have rules.
// Approach : Each state becomes a class that implements the allowed operations and decides which
//            state comes next. Real domain: an order lifecycle.
// Intuition: The alternative is a `status` field checked by every method - and each method grows its
//            own `if (status == ...)` ladder. The transition rules end up scattered across the class,
//            so "which transitions are legal?" has no single answer. State moves each behaviour INTO
//            the state itself, and the transition rules become explicit and local.
// Time     : O(1) per transition   Space: O(number of states) - states are usually stateless singletons
// Trade-off: One class per state, so a small machine gains ceremony for little benefit. It also
//            spreads the machine across files, making the whole picture harder to see at a glance -
//            which is why a diagram or a table in the notes is worth keeping alongside it.
// Real use  : order/payment/booking workflows, TCP connection states, thread lifecycle, game
//            character states, Spring StateMachine, document approval flows.

// ---- The state interface: every operation the context supports ----
interface OrderState {
    // Each method returns the NEXT state, which makes transitions explicit and returnable.
    OrderState pay(OrderContext ctx);
    OrderState ship(OrderContext ctx);
    OrderState cancel(OrderContext ctx);
    String name();

    // Shared rejection helper, so each state only writes the transitions it actually ALLOWS.
    default OrderState reject(String action) {
        System.out.println("      rejected: cannot " + action + " while " + name());
        return this;                       // stay in the current state
    }
}

// ---- Concrete states. Each knows ONLY its own legal transitions. ----
class PlacedState implements OrderState {
    public OrderState pay(OrderContext ctx) {
        System.out.println("      payment accepted");
        return new PaidState();
    }
    public OrderState ship(OrderContext ctx)   { return reject("ship (not paid yet)"); }
    public OrderState cancel(OrderContext ctx) {
        System.out.println("      order cancelled before payment - no refund needed");
        return new CancelledState();
    }
    public String name() { return "PLACED"; }
}

class PaidState implements OrderState {
    public OrderState pay(OrderContext ctx)  { return reject("pay again"); }
    public OrderState ship(OrderContext ctx) {
        System.out.println("      handed to courier");
        return new ShippedState();
    }
    public OrderState cancel(OrderContext ctx) {
        // A cancel AFTER payment has a different consequence - the refund. Putting this logic in the
        // state class keeps it next to the rule that requires it.
        System.out.println("      order cancelled - issuing refund of " + ctx.amount());
        return new CancelledState();
    }
    public String name() { return "PAID"; }
}

class ShippedState implements OrderState {
    public OrderState pay(OrderContext ctx)  { return reject("pay"); }
    public OrderState ship(OrderContext ctx) { return reject("ship again"); }
    // A shipped order cannot be cancelled - it must be returned instead. The rule lives here.
    public OrderState cancel(OrderContext ctx) { return reject("cancel (already shipped)"); }
    public String name() { return "SHIPPED"; }
}

// A TERMINAL state: every operation is rejected, which is expressed by simply not allowing any.
class CancelledState implements OrderState {
    public OrderState pay(OrderContext ctx)    { return reject("pay"); }
    public OrderState ship(OrderContext ctx)   { return reject("ship"); }
    public OrderState cancel(OrderContext ctx) { return reject("cancel again"); }
    public String name() { return "CANCELLED"; }
}

// ---- The CONTEXT: delegates every operation to its current state ----
class OrderContext {
    private OrderState state = new PlacedState();
    private final String id;
    private final double amount;

    OrderContext(String id, double amount) { this.id = id; this.amount = amount; }

    double amount() { return amount; }
    String state() { return state.name(); }

    // The context contains NO conditionals. It simply forwards and adopts whatever state comes back.
    void pay()    { System.out.println("  " + id + " [" + state.name() + "] pay()");    state = state.pay(this); }
    void ship()   { System.out.println("  " + id + " [" + state.name() + "] ship()");   state = state.ship(this); }
    void cancel() { System.out.println("  " + id + " [" + state.name() + "] cancel()"); state = state.cancel(this); }
}

public class StatePattern {
    public static void main(String[] args) {

        System.out.println("1. The happy path:");
        OrderContext order = new OrderContext("ORD-1", 1500);
        order.pay();
        order.ship();
        System.out.println("  final state: " + order.state());

        System.out.println("2. Illegal transitions are refused BY THE STATE, not by scattered ifs:");
        OrderContext order2 = new OrderContext("ORD-2", 800);
        order2.ship();      // not paid yet
        order2.pay();
        order2.pay();       // already paid
        order2.ship();
        order2.cancel();    // already shipped
        System.out.println("  final state: " + order2.state());

        System.out.println("3. The same action has DIFFERENT consequences per state:");
        OrderContext beforePayment = new OrderContext("ORD-3", 500);
        beforePayment.cancel();                    // no refund

        OrderContext afterPayment = new OrderContext("ORD-4", 2400);
        afterPayment.pay();
        afterPayment.cancel();                     // refund issued
        System.out.println("  cancel() behaved differently without a single if-statement.");
    }
}

/* ------------------------------ THE STATE MACHINE ------------------------------
 *
 *      PLACED ----pay()----> PAID ----ship()----> SHIPPED  (terminal for cancel)
 *        |                    |
 *     cancel()             cancel() + refund
 *        |                    |
 *        v                    v
 *              CANCELLED (terminal)
 *
 * | from \ action | pay()      | ship()     | cancel()          |
 * | PLACED        | -> PAID    | rejected   | -> CANCELLED      |
 * | PAID          | rejected   | -> SHIPPED | -> CANCELLED (+refund) |
 * | SHIPPED       | rejected   | rejected   | rejected          |
 * | CANCELLED     | rejected   | rejected   | rejected          |
 *
 * Keeping this table with the code matters: the pattern's main cost is that the machine is spread
 * across classes and no single file shows the whole picture.
 *
 * ------------------------------ WHAT IT REPLACES --------------------------------
 *     void ship() {
 *         if (status == PLACED)         throw new IllegalStateException("not paid");
 *         else if (status == PAID)      { status = SHIPPED; courier.send(); }
 *         else if (status == SHIPPED)   throw new IllegalStateException("already shipped");
 *         else if (status == CANCELLED) throw new IllegalStateException("cancelled");
 *     }
 * ...and the same ladder repeated in pay(), cancel(), refund(), and every future operation. Adding a
 * state means finding and updating EVERY one of them, and missing one is silent.
 *
 * ----------------------------- STATE vs STRATEGY --------------------------------
 * Structurally identical; the difference is who decides and whether it changes:
 *   STRATEGY - the CLIENT picks the algorithm; it does not change itself.
 *   STATE    - the object TRANSITIONS ITSELF as events occur; the client just sends events.
 * Here each method RETURNS the next state - that self-transition is the signature of State.
 *
 * ------------------------------- WHEN NOT TO USE --------------------------------
 * - Two or three states with trivial rules - an enum plus a switch is clearer and shows the whole
 *   machine in one place.
 * - Transitions are data-driven and numerous - a transition TABLE or a state-machine library scales
 *   better than a class per state.
 * - The states do not actually change behaviour, only a label - just use an enum field.
 * --------------------------------------------------------------------------------- */
