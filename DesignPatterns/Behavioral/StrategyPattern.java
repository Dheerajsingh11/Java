// Pattern  : STRATEGY (Behavioural)
// Problem  : Select one of several interchangeable ALGORITHMS at run time, without conditionals
//            spreading through the code.
// Approach : Each algorithm implements a common interface; the context holds one and delegates.
//            Real domain: e-commerce discount rules.
// Intuition: A growing `if/else` over "which pricing rule applies" is the smell this pattern fixes.
//            Every new rule means editing that method, re-testing it, and risking the existing
//            branches. Extracting each rule into its own class means adding a rule ADDS a file
//            instead of MODIFYING a tested one - the Open-Closed Principle in practice.
// Time     : O(1) delegation   Space: O(1) per strategy (they are usually stateless and shareable)
// Trade-off: More classes, and the caller must know which strategy to pick. In modern Java a
//            strategy with one method is just a lambda, which removes most of the boilerplate -
//            shown at the bottom of this file.
// Real use  : java.util.Comparator, Collections.sort(list, comparator), ThreadPoolExecutor's
//            RejectedExecutionHandler, Spring's PasswordEncoder, javax.crypto Cipher algorithms.

import java.util.List;
import java.util.function.Function;

// The strategy interface: one algorithm, one method.
interface DiscountStrategy {
    double apply(double amount);
    String name();
}

class NoDiscount implements DiscountStrategy {
    public double apply(double amount) { return amount; }
    public String name() { return "none"; }
}

class PercentageDiscount implements DiscountStrategy {
    private final double percent;
    PercentageDiscount(double percent) { this.percent = percent; }
    public double apply(double amount) { return amount - (amount * percent / 100); }
    public String name() { return percent + "% off"; }
}

class FlatDiscount implements DiscountStrategy {
    private final double off;
    FlatDiscount(double off) { this.off = off; }
    // Guard: a flat discount must never make the total negative.
    public double apply(double amount) { return Math.max(0, amount - off); }
    public String name() { return "flat " + off + " off"; }
}

class TieredDiscount implements DiscountStrategy {
    // Each strategy can carry its OWN logic and state without complicating the others.
    public double apply(double amount) {
        if (amount >= 5000) return amount * 0.80;      // 20% over 5000
        if (amount >= 2000) return amount * 0.90;      // 10% over 2000
        return amount;
    }
    public String name() { return "tiered"; }
}

// The CONTEXT: holds a strategy and delegates. It contains no pricing logic at all.
class Checkout {
    private DiscountStrategy discount = new NoDiscount();

    // The strategy can be swapped at RUN time - which a hard-coded if/else cannot do.
    void setDiscount(DiscountStrategy discount) { this.discount = discount; }

    double total(double subtotal) {
        double result = discount.apply(subtotal);
        System.out.printf("  %-14s %8.2f -> %8.2f%n", discount.name(), subtotal, result);
        return result;
    }
}

public class StrategyPattern {
    public static void main(String[] args) {

        Checkout checkout = new Checkout();
        double subtotal = 3000;

        System.out.println("Same context, different strategies swapped at run time:");
        for (DiscountStrategy s : List.of(
                new NoDiscount(),
                new PercentageDiscount(15),
                new FlatDiscount(500),
                new TieredDiscount())) {
            checkout.setDiscount(s);
            checkout.total(subtotal);
        }

        // Choosing from data - the case that makes Strategy genuinely necessary.
        System.out.println("Chosen from a coupon code:");
        String coupon = "SAVE15";
        DiscountStrategy fromCoupon = switch (coupon) {
            case "SAVE15" -> new PercentageDiscount(15);
            case "FLAT500" -> new FlatDiscount(500);
            default -> new NoDiscount();
        };
        checkout.setDiscount(fromCoupon);
        checkout.total(1200);

        // ---- Modern Java: a single-method strategy IS a lambda ----
        System.out.println("As lambdas - no class needed for simple strategies:");
        checkout.setDiscount(new DiscountStrategy() {
            public double apply(double a) { return a - 100; }
            public String name() { return "lambda -100"; }
        });
        checkout.total(1000);

        // If the interface were a plain Function, it would be even shorter:
        Function<Double, Double> halfOff = a -> a / 2;
        System.out.printf("  %-14s %8.2f -> %8.2f%n", "Function", 1000.0, halfOff.apply(1000.0));

        // Comparator is the Strategy pattern you already use every day.
        System.out.println("java.util.Comparator IS a strategy:");
        List<String> names = new java.util.ArrayList<>(List.of("Cara", "asha", "Bala"));
        names.sort(String::compareTo);
        System.out.println("  natural        : " + names);
        names.sort(String.CASE_INSENSITIVE_ORDER);
        System.out.println("  case-insensitive: " + names);
    }
}

/* ------------------------------ WHAT IT REPLACES ------------------------------
 * The conditional this pattern removes:
 *
 *     double total(double amount, String type) {
 *         if (type.equals("PERCENT"))    return amount * 0.85;
 *         else if (type.equals("FLAT"))  return amount - 500;
 *         else if (type.equals("TIERED")) { ... }
 *         return amount;
 *     }
 *
 * Every new rule edits this method. That means re-testing every existing branch, and one method
 * slowly accumulating unrelated logic. With Strategy, a new rule is a NEW FILE and nothing already
 * working is touched.
 *
 * ----------------------- STRATEGY vs ITS NEIGHBOURS ---------------------------
 * STRATEGY   swap an ALGORITHM; the caller chooses.       (behavioural, run-time)
 * STATE      behaviour changes as internal STATE changes; the object transitions itself.
 * BRIDGE     structural separation of two hierarchies; designed in, not swapped per call.
 * TEMPLATE   fixes the SKELETON and varies the steps via inheritance, not composition.
 *
 * Strategy and State look identical in code. The difference: with Strategy the CLIENT picks and the
 * strategy does not change itself; with State the object SWITCHES ITSELF as events occur.
 *
 * ------------------------------- WHEN NOT TO USE -------------------------------
 * - There are only two cases and they will never grow - an if/else is clearer than two classes.
 * - The algorithms need very different INPUTS; forcing them behind one interface produces awkward
 *   parameters that some implementations ignore.
 * - The choice is fixed at compile time and never varies - just call the method directly.
 * - In modern Java, prefer a lambda or a Function for a single-method strategy; a full interface
 *   plus classes is worth it when the strategies carry state or need names for logging.
 * -------------------------------------------------------------------------------- */
