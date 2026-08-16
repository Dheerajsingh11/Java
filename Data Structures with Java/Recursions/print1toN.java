// Problem  : Print the numbers 1, 2, 3, ... N in INCREASING order using recursion.
// Approach : Recurse FIRST (all the way down to the base case), then print on the way back up.
// Intuition: The trick is WHERE the print sits. Because the recursive call comes before the print,
//            nothing is printed on the way down; the printing happens as the stack UNWINDS, which
//            reverses the natural N..1 order into 1..N.
// Time     : THETA(n) - one call per number
// Space    : THETA(n) - n stack frames are live at the deepest point
// Trade-off: Compare with printNto1.java, which is identical except the print comes BEFORE the
//            recursive call and therefore prints in decreasing order. Same structure, opposite
//            output - a compact lesson in pre-order vs post-order processing.

public class print1toN {

    public static void main(String[] args) {
        int n = 5;
        print1toN(n);   // expected: 1 2 3 4 5 (each on its own line)
    }

    static void print1toN(int n) {
        // BASE CASE: nothing to print for 0 (or below); this also stops the recursion.
        if (n == 0) {
            return;
        }

        print1toN(n - 1);          // go ALL the way down first - no output happens yet
        System.out.println(n);     // printed while UNWINDING, so 1 prints before 2, 2 before 3, ...
    }

    // ---------------------------- IMPORTANT NOTE ----------------------------
    // This version is NOT tail-recursive: work (the print) still remains AFTER the recursive call
    // returns, so each frame must stay on the stack until its child finishes.
    //
    // A tail-recursive variant carries the counter down as a parameter, doing its work BEFORE the
    // call, so nothing is pending on return:
    //
    //   static void print1toNTail(int n, int k) {
    //       if (k > n) return;             // base case
    //       System.out.println(k);         // work happens BEFORE the recursive call
    //       print1toNTail(n, k + 1);       // nothing left to do after this returns
    //   }
    //
    // Caveat: the JVM does NOT perform tail-call optimization, so in Java this still uses O(n)
    // stack. The distinction matters conceptually (and in languages that do optimize it).
}
