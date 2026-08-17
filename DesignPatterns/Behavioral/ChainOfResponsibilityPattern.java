// Pattern  : CHAIN OF RESPONSIBILITY (Behavioural)
// Problem  : Pass a request along a chain of potential handlers until one of them deals with it.
// Approach : Each handler holds a reference to the next; it either handles the request or forwards
//            it. Real domain: expense approval by amount (manager -> director -> CFO -> board).
// Intuition: The sender should not need to know WHICH handler is right - that knowledge would mean a
//            big conditional at the call site, updated every time the rules change. A chain moves the
//            decision into the handlers themselves: each knows only its own limit and who comes next.
//            The chain can then be rebuilt from configuration without touching the sender.
// Time     : O(n) in the chain length   Space: O(1) iterative, or O(n) stack if recursive
// Trade-off: There is NO GUARANTEE a request is handled - it can fall off the end of the chain, so a
//            terminal handler or an explicit "unhandled" outcome is essential. Debugging is also
//            harder: you must trace the chain to see who actually responded.
// Real use  : Servlet filters and Spring's FilterChain, logging framework handler levels,
//            java.util.logging Handler chains, Netty's ChannelPipeline, middleware in most web
//            frameworks, exception propagation up the call stack.

// ---- The request ----
record ExpenseRequest(String requester, String purpose, double amount) { }

// ---- The handler ----
abstract class Approver {
    private Approver next;                      // the link that forms the chain

    // Returns `this` so the chain can be built fluently: a.linkTo(b).linkTo(c)
    Approver linkTo(Approver next) { this.next = next; return next; }

    // TEMPLATE METHOD: the traversal logic is written ONCE here; subclasses supply only the policy.
    final void handle(ExpenseRequest request) {
        if (canApprove(request)) {
            approve(request);
            return;                             // handled - the chain STOPS here
        }
        if (next != null) {
            System.out.println("      " + title() + " cannot approve - escalating");
            next.handle(request);               // pass it along
        } else {
            // The end of the chain. Without this branch a request would vanish silently, which is
            // this pattern's most common failure mode.
            System.out.println("      [!] no approver in the chain can authorise "
                    + request.amount() + " - request DENIED");
        }
    }

    protected abstract boolean canApprove(ExpenseRequest r);
    protected abstract String title();
    protected void approve(ExpenseRequest r) {
        System.out.println("      APPROVED by " + title() + " (" + r.purpose() + ", " + r.amount() + ")");
    }
}

class TeamLead extends Approver {
    protected boolean canApprove(ExpenseRequest r) { return r.amount() <= 1_000; }
    protected String title() { return "Team Lead"; }
}

class Manager extends Approver {
    protected boolean canApprove(ExpenseRequest r) { return r.amount() <= 10_000; }
    protected String title() { return "Manager"; }
}

class Director extends Approver {
    protected boolean canApprove(ExpenseRequest r) { return r.amount() <= 50_000; }
    protected String title() { return "Director"; }
}

class CFO extends Approver {
    // The CFO has an extra RULE, not just a bigger limit - handlers may differ in kind, not only degree.
    protected boolean canApprove(ExpenseRequest r) {
        return r.amount() <= 500_000 && !r.purpose().toLowerCase().contains("acquisition");
    }
    protected String title() { return "CFO"; }
}

public class ChainOfResponsibilityPattern {
    public static void main(String[] args) {

        // Build the chain. Order encodes the escalation policy and can come from configuration.
        Approver lead = new TeamLead();
        lead.linkTo(new Manager()).linkTo(new Director()).linkTo(new CFO());

        System.out.println("Escalation by amount:");
        for (ExpenseRequest r : new ExpenseRequest[]{
                new ExpenseRequest("asha", "team lunch", 800),
                new ExpenseRequest("bala", "laptops", 8_000),
                new ExpenseRequest("cara", "conference booth", 45_000),
                new ExpenseRequest("dev",  "new office fit-out", 300_000) }) {
            System.out.println("  " + r.requester() + " requests " + r.amount() + " for " + r.purpose());
            lead.handle(r);
        }

        System.out.println("A request nobody can approve falls off the end:");
        lead.handle(new ExpenseRequest("eve", "building purchase", 9_000_000));

        System.out.println("A rule, not just a limit - the CFO refuses acquisitions at any size:");
        lead.handle(new ExpenseRequest("frank", "competitor acquisition", 200_000));

        System.out.println("Rebuilding the chain changes policy WITHOUT touching the sender:");
        Approver strict = new Director();                 // team leads and managers removed
        strict.linkTo(new CFO());
        strict.handle(new ExpenseRequest("asha", "team lunch", 800));
    }
}

/* ------------------------------ WHAT IT REPLACES ------------------------------
 *     void approve(ExpenseRequest r) {
 *         if (r.amount() <= 1000)        teamLead.approve(r);
 *         else if (r.amount() <= 10000)  manager.approve(r);
 *         else if (r.amount() <= 50000)  director.approve(r);
 *         else if (r.amount() <= 500000) cfo.approve(r);
 *         else                           throw new IllegalStateException("too large");
 *     }
 *
 * The caller now knows every approver, every threshold, and the escalation order. Changing a limit,
 * inserting a new approver, or reordering the chain all mean editing this method. With a chain, the
 * caller knows only the FIRST handler.
 *
 * ------------------------------- TWO VARIATIONS --------------------------------
 * 1. STOP AT THE FIRST HANDLER (used here) - exactly one handler responds. Approval workflows,
 *    exception handling, routing.
 * 2. LET EVERY HANDLER SEE IT - each does its part and always forwards. Servlet filters and
 *    middleware work this way: authentication, then logging, then compression, then the request.
 * The second is really a PIPELINE; the difference is whether handling terminates the traversal.
 *
 * ---------------------------- THE MAIN RISK ------------------------------------
 * A request can reach the end unhandled. Always either terminate the chain with a catch-all handler
 * or make "unhandled" an explicit, visible outcome as above. Silent disappearance is the failure
 * mode that makes this pattern hard to debug.
 *
 * Also beware CYCLES: linking a handler back into the chain produces infinite recursion.
 *
 * ------------------------------ WHEN NOT TO USE --------------------------------
 * - Exactly one handler is ever correct and it is known statically - call it directly.
 * - The chain is short and fixed - a switch is clearer and shows the whole policy in one place.
 * - Every request traverses a long chain in a hot path - the O(n) walk becomes real overhead.
 * -------------------------------------------------------------------------------- */
