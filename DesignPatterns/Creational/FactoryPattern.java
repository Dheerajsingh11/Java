// Pattern  : FACTORY METHOD (Creational)
// Problem  : Create objects without the calling code naming the concrete classes it depends on.
// Approach : A factory decides which implementation to build from a request; callers work only with
//            the interface. Real domain: a payment gateway choosing Card / UPI / NetBanking.
// Intuition: The moment client code writes `new CardPayment()`, it is welded to that class. Adding
//            a payment method then means editing every such call site. A factory concentrates all
//            those decisions in ONE place, so callers depend on the abstraction and adding a new
//            type touches exactly one file.
// Time     : O(1) creation   Space: O(1)
// Trade-off: Adds a layer of indirection and one more class. Worth it when the set of types grows or
//            varies at run time (config, user input, feature flags); pure overhead when there is
//            only ever one implementation. It also improves TESTABILITY - a test can supply a fake
//            factory instead of the real gateway.
// Real use  : java.util.Calendar.getInstance(), NumberFormat.getInstance(), Spring's BeanFactory,
//            JDBC DriverManager.getConnection(), ThreadPoolExecutor's ThreadFactory.

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// The abstraction every caller sees. Callers never mention a concrete class.
interface PaymentMethod {
    String pay(double amount);
    double feeFor(double amount);
}

class CardPayment implements PaymentMethod {
    public String pay(double amount) {
        return String.format("Charged %.2f to card (fee %.2f)", amount, feeFor(amount));
    }
    public double feeFor(double amount) { return amount * 0.02; }      // 2% card fee
}

class UpiPayment implements PaymentMethod {
    public String pay(double amount) {
        return String.format("UPI request for %.2f (fee %.2f)", amount, feeFor(amount));
    }
    public double feeFor(double amount) { return 0; }                   // UPI is free
}

class NetBankingPayment implements PaymentMethod {
    public String pay(double amount) {
        return String.format("Net-banking transfer of %.2f (fee %.2f)", amount, feeFor(amount));
    }
    public double feeFor(double amount) { return 5.0; }                 // flat fee
}

// ---------------------------------------------------------------------------
// VERSION 1 - the simple factory: a switch in one place.
// ---------------------------------------------------------------------------
class PaymentFactory {
    static PaymentMethod create(String type) {
        // This switch is the ONLY place that knows the concrete classes. Every caller is insulated
        // from them, so adding a type is a one-file change instead of a codebase-wide edit.
        return switch (type.toUpperCase()) {
            case "CARD"        -> new CardPayment();
            case "UPI"         -> new UpiPayment();
            case "NETBANKING"  -> new NetBankingPayment();
            default -> throw new IllegalArgumentException("Unsupported payment method: " + type);
        };
    }
}

// ---------------------------------------------------------------------------
// VERSION 2 - registry-based, so new types can be added WITHOUT editing the factory.
// This is the Open-Closed Principle applied to the factory itself (see SOLID/O_OpenClosed).
// ---------------------------------------------------------------------------
class RegistryPaymentFactory {
    private static final Map<String, Supplier<PaymentMethod>> REGISTRY = new HashMap<>();

    static { // seed the built-in types
        register("CARD", CardPayment::new);
        register("UPI", UpiPayment::new);
        register("NETBANKING", NetBankingPayment::new);
    }

    static void register(String type, Supplier<PaymentMethod> creator) {
        REGISTRY.put(type.toUpperCase(), creator);   // a plugin can add its own type at run time
    }

    static PaymentMethod create(String type) {
        Supplier<PaymentMethod> creator = REGISTRY.get(type.toUpperCase());
        if (creator == null) throw new IllegalArgumentException("Unsupported: " + type);
        return creator.get();
    }
}

public class FactoryPattern {
    public static void main(String[] args) {

        // The caller names only the INTERFACE. It has no idea which class it received, which is
        // exactly what lets the implementation change without touching this code.
        System.out.println("Simple factory:");
        for (String type : new String[]{ "CARD", "UPI", "NETBANKING" }) {
            PaymentMethod method = PaymentFactory.create(type);
            System.out.println("  " + method.pay(1000));
        }

        // Choosing at RUN TIME from data - the case that makes a factory genuinely necessary.
        String fromConfig = "UPI";
        System.out.println("chosen from config: " + PaymentFactory.create(fromConfig).pay(250));

        // Registry version: a new method is added without modifying the factory's source.
        System.out.println("Registry factory - adding a NEW type at run time:");
        RegistryPaymentFactory.register("CRYPTO", () -> new PaymentMethod() {
            public String pay(double amount) { return String.format("Crypto transfer of %.2f", amount); }
            public double feeFor(double amount) { return amount * 0.01; }
        });
        System.out.println("  " + RegistryPaymentFactory.create("CRYPTO").pay(500));

        // Unknown types fail loudly rather than silently returning null.
        try {
            PaymentFactory.create("CHEQUE");
        } catch (IllegalArgumentException e) {
            System.out.println("  rejected: " + e.getMessage());
        }
    }
}

/* ------------------------- FACTORY METHOD vs ABSTRACT FACTORY -------------------------
 * FACTORY METHOD (this file)  creates ONE product from a family. "Give me a PaymentMethod."
 * ABSTRACT FACTORY            creates a FAMILY of related products that must match each other.
 *                             "Give me a whole UI toolkit whose button, checkbox and menu all
 *                              belong to the same look and feel." See AbstractFactoryPattern.java.
 *
 * ------------------------------------ WHEN NOT TO USE ---------------------------------
 * - There is exactly one implementation and no prospect of another - `new` is clearer.
 * - The construction is trivial and the type is known at compile time.
 * - You are wrapping a constructor in a factory "for flexibility" you have no evidence you need.
 *   Indirection has a real readability cost; add it when the second implementation arrives.
 * -------------------------------------------------------------------------------------- */
