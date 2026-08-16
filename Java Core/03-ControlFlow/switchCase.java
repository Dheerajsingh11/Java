// Problem  : Branch to one of many actions by matching a variable against fixed constant values.
// Approach : Use a switch statement with the modern arrow (->) syntax, plus notes on the classic
//            colon syntax and fall-through.
// Intuition: switch jumps directly to the matching label instead of testing conditions one by one,
//            which reads cleanly when comparing ONE value against many constants.
// Time     : O(1) amortized (the JVM can compile dense int switches into a jump table)   Space: O(1)
// Trade-off: switch only compares for EQUALITY against compile-time constants (int, char, String,
//            enum, ...). For ranges or complex boolean tests, use if/else-if (see ifElse.java).

public class switchCase {
    public static void main(String[] args) {
        int num = 20;

        // ---- Modern arrow syntax (Java 14+) ----
        // Each "case X ->" runs ONLY its own branch. There is NO fall-through, so no "break" is
        // needed - this avoids the classic bug of forgetting break.
        switch (num) {
            case 5  -> System.out.println("It is 5");
            case 10 -> System.out.println("It is 10");
            case 15 -> System.out.println("It is 15");
            case 20 -> System.out.println("It is 20");   // matches -> prints this, then done
            default -> System.out.println("Not present"); // runs if nothing above matched
        }
        // For num = 20 -> prints "It is 20".

        // ---- Classic colon syntax + intentional fall-through ----
        // With "case X:" execution FALLS THROUGH to the next case until it hits a break. This is a
        // common source of bugs, but is occasionally useful to group labels that share code.
        char grade = 'B';
        switch (grade) {
            case 'A':
            case 'B':                                   // 'A' and 'B' fall through to the same block
                System.out.println("Great job");
                break;                                  // break stops the fall-through
            case 'C':
                System.out.println("You passed");
                break;
            default:
                System.out.println("Needs improvement");
        }
        // For grade = 'B' -> prints "Great job".

        // ---- switch as an EXPRESSION (Java 14+): it can yield a value ----
        int day = 3;
        String name = switch (day) {
            case 1 -> "Mon";
            case 2 -> "Tue";
            case 3 -> "Wed";
            default -> "Other";
        };
        System.out.println("Day " + day + " = " + name); // expected: Day 3 = Wed
    }
}
