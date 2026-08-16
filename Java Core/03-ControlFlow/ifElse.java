// Problem  : Choose different actions based on conditions using if / else-if / else.
// Approach : Show a full if / else-if / else ladder and explain how conditions are tested in order.
// Intuition: The program checks each condition top to bottom and runs the FIRST block whose
//            condition is true, then skips the rest. "else" is the catch-all when none matched.
// Time     : O(1) here (fixed checks)   Space: O(1)
// Trade-off: An if/else-if ladder is ideal for RANGE checks and boolean conditions. When you are
//            comparing one variable against many fixed constant values, a switch (see switchCase.java)
//            is cleaner.

public class ifElse {
    public static void main(String[] args) {
        int a = 15;

        // Conditions are evaluated top-down. Order the branches so earlier ones are the more
        // specific / narrower cases, because the first true branch wins and the rest are skipped.
        if (a < 10) {
            // runs only when a is strictly less than 10
            System.out.println("a is less than 10");
        } else if (a < 20) {
            // reached ONLY if the first was false, i.e. a >= 10, and now a < 20  => 10 <= a < 20
            System.out.println("a is between 10 and 19");
        } else {
            // catch-all: every earlier condition was false, so a >= 20
            System.out.println("a is 20 or more");
        }
        // For a = 15: first false, second true -> prints "a is between 10 and 19".

        // Note on the original version's bug: writing "if (a <= 10) ... else if (a > 10) ... else"
        // makes the final else UNREACHABLE, because a is always either <= 10 or > 10. Structure
        // ladders so the else can actually be hit (or drop it if truly exhaustive).

        // Common pitfall: use == for comparison, not = (assignment). For objects, use .equals()
        // for value equality; == on objects compares references, not contents.
    }
}
