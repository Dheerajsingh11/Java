// Problem  : Model a CLOSED set of subtypes and branch over them with compile-time exhaustiveness.
// Approach : `sealed` interfaces/classes restrict who may implement them; pattern matching for
//            `instanceof` and `switch` then destructures the value without manual casting.
// Intuition: A normal interface is open - anyone can add an implementation, so the compiler can never
//            know you have handled every case. `sealed` closes the set, which lets the compiler
//            VERIFY a switch is exhaustive. Add a new subtype later and every incomplete switch
//            becomes a compile error instead of a run-time surprise.
// Time     : n/a   Space: n/a
// Trade-off: You give up open extensibility and gain exhaustiveness checking - a good trade when the
//            set of cases is genuinely fixed (shapes, states, results, AST nodes) and a bad one for
//            plugin-style APIs meant to be extended by others.
// REQUIRES : Java 21+ (sealed types: 17+, pattern matching for switch: 21+).

import java.util.List;

// `sealed` + `permits`: only these three types may implement Shape. Each must be declared
// final, sealed, or non-sealed so the hierarchy stays closed.
sealed interface Shape permits Circle, Rectangle, Triangle { }

record Circle(double radius) implements Shape { }
record Rectangle(double width, double height) implements Shape { }
record Triangle(double base, double height) implements Shape { }

// A sealed hierarchy also models "one of N outcomes" cleanly.
sealed interface Result permits Success, Failure { }
record Success(String value) implements Result { }
record Failure(String error) implements Result { }

public class SealedAndPatternMatching {

    // ---- Pattern matching for switch, over a sealed type ----
    // No `default` branch is needed: the compiler knows Shape has exactly three permitted subtypes.
    // Add a fourth and THIS METHOD STOPS COMPILING - which is exactly the safety we want.
    static double area(Shape s) {
        return switch (s) {
            case Circle c    -> Math.PI * c.radius() * c.radius();
            case Rectangle r -> r.width() * r.height();
            case Triangle t  -> 0.5 * t.base() * t.height();
        };
    }

    // ---- Guarded patterns: add a condition with `when` ----
    static String describe(Shape s) {
        return switch (s) {
            case Circle c when c.radius() > 10 -> "large circle";
            case Circle c                      -> "circle r=" + c.radius();
            case Rectangle r when r.width() == r.height() -> "square " + r.width();
            case Rectangle r                   -> "rectangle";
            case Triangle t                    -> "triangle";
        };
    }

    // ---- Record deconstruction: bind the components directly ----
    static String corners(Shape s) {
        return switch (s) {
            case Circle ignored          -> "no corners";
            case Rectangle(double w, double h) -> "4 corners (" + w + "x" + h + ")";
            case Triangle(double b, double h)  -> "3 corners (base " + b + ")";
        };
    }

    static String handle(Result r) {
        return switch (r) {
            case Success s -> "OK: " + s.value();
            case Failure f -> "FAILED: " + f.error();
        };
    }

    public static void main(String[] args) {

        List<Shape> shapes = List.of(new Circle(2), new Rectangle(3, 3), new Triangle(4, 5), new Circle(12));
        for (Shape s : shapes) {
            System.out.printf("%-28s area=%6.2f  %s  |  %s%n",
                    s, area(s), describe(s), corners(s));
        }

        System.out.println(handle(new Success("saved")));
        System.out.println(handle(new Failure("disk full")));

        // ---- Pattern matching for instanceof (Java 16+) ----
        // The old form required a redundant cast on the very next line:
        //     if (o instanceof String) { String str = (String) o; ... }
        Object o = "hello world";
        if (o instanceof String str && str.length() > 3) {   // binds `str` and can use it immediately
            System.out.println("length = " + str.length());
        }

        // null is not matched by a type pattern, so this is null-safe by construction.
        Object nothing = null;
        System.out.println("null matches String? " + (nothing instanceof String s2));
    }
}

/* ------------------------------- WHY SEALED MATTERS -------------------------------
 * Without sealing, a switch over subtypes needs a `default` branch that usually throws:
 *
 *     default -> throw new IllegalStateException("unhandled shape");
 *
 * That defers the error to RUN TIME, and only for inputs you happen to exercise. With a sealed
 * hierarchy the compiler proves the switch is exhaustive, so forgetting a case is a BUILD failure.
 * This is the same guarantee an enum switch gives you, extended to types that carry data.
 *
 * ---------------------------------- WHEN TO USE ----------------------------------
 *   - A fixed set of variants: shapes, tokens/AST nodes, protocol messages, state machines.
 *   - Result / Either types: Success | Failure, as above.
 *   - Anywhere you would otherwise write a chain of instanceof checks with casts.
 *
 * -------------------------------- WHEN NOT TO USE --------------------------------
 *   - PUBLIC EXTENSION POINTS. If third parties should be able to add implementations, sealing
 *     forbids exactly that - use a plain interface.
 *   - Rapidly changing hierarchies: every added subtype breaks every switch (helpful for
 *     correctness, painful during exploration).
 *   - Behaviour that belongs ON the type. If each variant simply computes its own area, a plain
 *     polymorphic method is cleaner than an external switch. Sealed types shine when the OPERATIONS
 *     vary and live outside the data (the classic "expression problem" trade-off).
 * ---------------------------------------------------------------------------------- */
