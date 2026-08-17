// Pattern  : ADAPTER (Structural)
// Problem  : Make an existing class work with an interface it was never designed to implement.
// Approach : A wrapper implements the interface your code expects and translates each call into the
//            legacy API underneath. Real domain: a legacy payment gateway behind a modern interface.
// Intuition: You rarely control every class you must use - a vendor SDK, a legacy module, an old
//            internal library. Rewriting them is impossible or unwise. An adapter is the electrical
//            plug adapter of software: it changes the SHAPE of the interface without changing the
//            device. Your code stays clean and the legacy code stays untouched.
// Time     : O(1) delegation overhead   Space: O(1) per adapter
// Trade-off: Adds one indirection layer per adapted type, and a place where translation bugs can
//            hide (unit mismatches, silently swallowed errors). That is far cheaper than either
//            rewriting the legacy system or letting its awkward API leak through your whole codebase.
// Real use  : java.util.Arrays.asList(), InputStreamReader (byte stream -> char stream),
//            java.io.OutputStreamWriter, Spring's HandlerAdapter, JDBC drivers.

import java.util.HashMap;
import java.util.Map;

// ---- The interface OUR application is written against ----
interface PaymentProcessor {
    // Modern, clean contract: amount in RUPEES, an order id, returns a structured result.
    PaymentResult charge(String orderId, double amountRupees);
}

record PaymentResult(boolean success, String reference, String message) { }

// ---- The LEGACY gateway we cannot change ----
// Note everything wrong with it from our perspective: amounts in PAISE (integer), a different
// method name, a raw Map return value, and errors signalled by a status STRING.
class LegacyPaymentGateway {
    private int counter = 1000;

    Map<String, Object> doTransaction(int amountInPaise, String merchantRef) {
        Map<String, Object> response = new HashMap<>();
        if (amountInPaise <= 0) {
            response.put("status", "FAILED");
            response.put("errorText", "amount must be positive");
            return response;
        }
        response.put("status", "OK");
        response.put("txnId", "LEGACY-" + (++counter));
        response.put("merchantRef", merchantRef);
        return response;
    }
}

// ---- THE ADAPTER: speaks PaymentProcessor outwards, LegacyPaymentGateway inwards ----
class LegacyGatewayAdapter implements PaymentProcessor {

    // OBJECT adapter: it HOLDS the adaptee (composition) rather than extending it. This is almost
    // always preferable to a "class adapter" that inherits, because Java allows only one superclass
    // and because composition does not expose the legacy API to our callers.
    private final LegacyPaymentGateway legacy;

    LegacyGatewayAdapter(LegacyPaymentGateway legacy) { this.legacy = legacy; }

    @Override
    public PaymentResult charge(String orderId, double amountRupees) {
        // TRANSLATION 1 - units. Rupees to paise. Getting this wrong by a factor of 100 is exactly
        // the class of bug adapters are prone to, which is why the conversion lives in ONE place.
        int paise = (int) Math.round(amountRupees * 100);

        // TRANSLATION 2 - call the differently-named legacy method.
        Map<String, Object> raw = legacy.doTransaction(paise, orderId);

        // TRANSLATION 3 - a stringly-typed status becomes a typed result. The awkwardness stops here
        // instead of spreading through every caller.
        if ("OK".equals(raw.get("status"))) {
            return new PaymentResult(true, (String) raw.get("txnId"), "charged " + amountRupees);
        }
        return new PaymentResult(false, null, (String) raw.get("errorText"));
    }
}

// A modern implementation of the SAME interface, to show the point: callers cannot tell them apart.
class ModernPaymentProcessor implements PaymentProcessor {
    public PaymentResult charge(String orderId, double amountRupees) {
        if (amountRupees <= 0) return new PaymentResult(false, null, "invalid amount");
        return new PaymentResult(true, "MOD-" + orderId, "charged " + amountRupees);
    }
}

public class AdapterPattern {

    // The client depends ONLY on PaymentProcessor. It has no idea whether a 20-year-old gateway or
    // a modern service is doing the work - which is the entire benefit.
    static void checkout(PaymentProcessor processor, String orderId, double amount) {
        PaymentResult result = processor.charge(orderId, amount);
        System.out.println(result.success()
                ? "  OK      ref=" + result.reference() + "  (" + result.message() + ")"
                : "  FAILED  " + result.message());
    }

    public static void main(String[] args) {
        System.out.println("Through the LEGACY gateway (adapted):");
        PaymentProcessor adapted = new LegacyGatewayAdapter(new LegacyPaymentGateway());
        checkout(adapted, "ORD-1", 499.50);      // 499.50 rupees -> 49950 paise internally
        checkout(adapted, "ORD-2", -5);          // legacy error text surfaces as a typed failure

        System.out.println("Through the MODERN processor (same interface):");
        checkout(new ModernPaymentProcessor(), "ORD-3", 499.50);
        checkout(new ModernPaymentProcessor(), "ORD-4", -5);

        System.out.println("The client code above is identical for both - that is the point.");
    }
}

/* ----------------------------- OBJECT vs CLASS ADAPTER -----------------------------
 * OBJECT adapter (used here)  - the adapter HOLDS the adaptee.
 *     + works even if the adaptee is final; can adapt several adaptees; does not leak their API
 * CLASS adapter               - the adapter EXTENDS the adaptee.
 *     - impossible in Java when you also need to extend something else; exposes the legacy methods
 * Prefer the object adapter.
 *
 * ----------------------------- ADAPTER vs ITS NEIGHBOURS ---------------------------
 * ADAPTER   changes an interface to one the client already expects.  (compatibility, after the fact)
 * FACADE    provides a SIMPLER interface over a complex subsystem.   (simplification)
 * DECORATOR keeps the SAME interface and adds behaviour.             (enhancement)
 * BRIDGE    separates abstraction from implementation up front.      (designed in, not retrofitted)
 *
 * The distinction from Facade catches people out: an adapter usually wraps ONE class to change its
 * shape; a facade wraps MANY to reduce their complexity.
 *
 * -------------------------------- WHEN NOT TO USE ----------------------------------
 * - You CAN change the source: fix the interface directly instead of wrapping it forever.
 * - The two interfaces are already compatible - the layer earns nothing.
 * - The translation would be lossy or ambiguous (the legacy API cannot express what the new one
 *   promises). An adapter that silently drops information is worse than no adapter.
 * ----------------------------------------------------------------------------------- */
