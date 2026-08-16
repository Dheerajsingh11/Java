// Problem  : Print the numbers N, N-1, ... 1 in DECREASING order using recursion.
// Approach : Print FIRST, then recurse. The current value is emitted on the way DOWN the recursion.
// Intuition: Because the print happens before the recursive call, each value is output the moment
//            its frame is entered - so the largest value prints first and the order is N..1.
// Time     : THETA(n) - one call per number
// Space    : THETA(n) - all n frames are on the call stack at the deepest point
// Trade-off: This is the mirror image of print1toN.java. The ONLY difference is the order of the
//            print and the recursive call, yet it flips the output completely. That contrast is the
//            clearest illustration of "process before recursing" (pre-order) versus "process after
//            recursing" (post-order) - the same idea that distinguishes tree traversals.

public class printNto1 {

    public static void main(String[] args) {
        int n = 5;
        printNto1(n);   // expected: 5 4 3 2 1 (each on its own line)
    }

    static void printNto1(int n) {
        // BASE CASE: nothing left to print, and it terminates the recursion.
        if (n == 0) {
            return;
        }

        System.out.println(n);     // emit the CURRENT value immediately (on the way down)
        printNto1(n - 1);          // then hand the smaller problem to the next call
    }
    // This version IS effectively tail-recursive: no work remains after the recursive call returns.
    // (Java still allocates a frame per call, since the JVM does not optimize tail calls.)
}
