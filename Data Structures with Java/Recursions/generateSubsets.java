// Problem  : Generate every subset (the power set) of a string's characters.
//            I/P: "AB"  ->  O/P: "" "A" "B" "AB"
// Approach : For each character, branch twice - EXCLUDE it, or INCLUDE it - and recurse to the next
//            index. The base case (index == length) means a complete decision set has been made.
// Intuition: Every character is an independent yes/no choice, so the recursion tree is a binary
//            decision tree of depth n with 2^n leaves - one leaf per subset. The string 'curr'
//            carries the choices made so far down each path.
// Time     : O(n * 2^n) - 2^n subsets, and building/printing each costs up to O(n)
// Space    : O(n) recursion depth (plus O(n) for the current partial string)
// Trade-off: 2^n output is inherent - you cannot enumerate a power set faster than its own size.
//            This is the same include/exclude skeleton used in Backtracking/subsets.java; the
//            difference is that this version builds new strings instead of undoing choices in place.

public class generateSubsets {
    public static void main(String[] args) {
        String s = "AB";
        // Start with an EMPTY accumulator (not " " - a space would pollute every printed subset).
        subset(s, "", 0);
        System.out.println();
        // expected:  "" A B AB   (the empty subset prints as nothing between the separators)

        subset("ABC", "", 0);   // 2^3 = 8 subsets
        System.out.println();
    }

    // s    = the source string
    // curr = characters chosen so far on this path
    // i    = index currently being decided
    static void subset(String s, String curr, int i) {
        // BASE CASE: every character has been decided, so 'curr' is one complete subset.
        if (i == s.length()) {
            System.out.print("[" + curr + "] ");
            return;
        }

        // BRANCH 1 - EXCLUDE s.charAt(i): move on without adding it.
        subset(s, curr, i + 1);

        // BRANCH 2 - INCLUDE s.charAt(i): append it, then move on.
        // Because String is immutable, "curr + charAt(i)" creates a NEW string, so the exclude
        // branch above is completely unaffected - no manual undo/backtracking step is needed.
        subset(s, curr + s.charAt(i), i + 1);
    }
    // Why exactly 2^n subsets: n independent binary choices => 2 * 2 * ... * 2 (n times) = 2^n.
}
