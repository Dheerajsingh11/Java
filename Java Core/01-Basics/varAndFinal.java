// Problem  : Use `final` to prevent reassignment and `var` to infer local variable types.
// Approach : Show what each keyword does, what it does NOT do, and where each helps or hurts.
// Intuition: `final` is about IMMUTABILITY OF THE BINDING (this name will never point elsewhere);
//            `var` is about NOT REPEATING a type the compiler can already see. Neither changes what
//            the program does at run time - both are about communicating intent to readers.
// Time     : n/a (compile-time)   Space: n/a
// Trade-off: `final` costs a keyword and buys a guarantee. `var` saves typing but can HIDE the type
//            from a reader - use it only when the type is obvious from the right-hand side.

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class varAndFinal {

    static final double TAX_RATE = 0.18;      // a constant: static final, SCREAMING_SNAKE_CASE

    public static void main(String[] args) {

        // ---------------- final ----------------
        final int limit = 10;
        // limit = 20;                        // does NOT compile - the binding is fixed

        // CRITICAL DISTINCTION: final freezes the REFERENCE, not the object it points to.
        final List<String> names = new ArrayList<>();
        names.add("Asha");                    // ALLOWED - we are mutating the object
        names.add("Bala");
        // names = new ArrayList<>();         // NOT allowed - that would rebind the reference
        System.out.println("final list can still be mutated: " + names);

        // To get a genuinely unchangeable collection you need an immutable one, not just `final`:
        final List<String> frozen = List.of("x", "y");
        try { frozen.add("z"); }
        catch (UnsupportedOperationException e) { System.out.println("List.of is truly immutable"); }

        // ---------------- var (Java 10+) ----------------
        // The compiler infers the type from the initializer. The variable is still STATICALLY typed -
        // this is not JavaScript's `var` and nothing is dynamic.
        var count = 42;                       // inferred int
        var message = "hello";                // inferred String
        var list = new ArrayList<String>();   // inferred ArrayList<String>
        // count = "text";                    // does NOT compile - count is an int, permanently

        System.out.println(count + " " + message.toUpperCase() + " " + list.size());

        // Where var genuinely helps: long generic types that add noise without adding information.
        var scores = new java.util.HashMap<String, List<Integer>>();
        scores.put("asha", List.of(90, 85));
        for (var entry : scores.entrySet()) {                    // Map.Entry<String, List<Integer>>
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // Combined: a local that is both inferred and unreassignable.
        final var total = 100 * TAX_RATE;
        System.out.println("total = " + total);
    }
}

/* --------------------------------- WHEN TO USE ---------------------------------
 *
 * final
 *   - CONSTANTS: `static final` for values that never change. Always.
 *   - Fields of an IMMUTABLE class - final fields are what make immutability enforceable
 *     (see 07-OOP/ImmutableClass.java).
 *   - Method parameters and locals you do not intend to reassign: it documents intent and stops a
 *     whole class of "I accidentally overwrote that" bugs.
 *   - Required in practice for variables captured by a lambda - they must be final or
 *     "effectively final" (never reassigned).
 *   - `final` on a METHOD prevents overriding; on a CLASS prevents subclassing.
 *
 * var  - use when the type is OBVIOUS from the right-hand side:
 *   GOOD:  var users = new ArrayList<User>();        // type is right there
 *          var count = 0;
 *   AVOID: var result = process();                   // what does process() return? unclear
 *          var x = getConfig().getValue();           // reader must go hunting
 *
 * ---------------------------------- LIMITS OF var --------------------------------
 *   - LOCAL VARIABLES ONLY. Not fields, not method parameters, not return types.
 *   - Requires an initializer (`var x;` cannot infer anything).
 *   - Cannot be initialized to null (no type to infer).
 *   - Does not work with lambdas directly: `var f = () -> {}` has no inferable target type.
 *
 * -------------------------------- COMMON CONFUSION -------------------------------
 * `final` does NOT mean immutable. A `final` reference to a mutable object still lets you change
 * that object - as the ArrayList above demonstrates. Immutability is a property of the TYPE;
 * final is a property of the VARIABLE.
 * -------------------------------------------------------------------------------- */
