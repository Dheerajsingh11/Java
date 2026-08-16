// Problem  : Cut a rope of length n into pieces whose lengths are all from the set {a, b, c}, and
//            return the MAXIMUM number of pieces possible (-1 if no exact cutting exists).
//            I/P: n=5, a=2, b=5, c=1  -> O/P: 5   (five pieces of length 1)
//            I/P: n=23, a=12, b=9, c=11 -> O/P: 2 (pieces of 11 and 12)
// Approach : NAIVE recursion - try all three cut lengths at every step and keep the best result.
// Intuition: The best solution for length n is "make one cut of a, b, or c, then solve optimally for
//            what remains". Since we cannot know in advance which first cut is best, we try all three
//            and take the maximum - adding 1 for the cut we just made.
// Time     : O(3^n) - three branches at every level of the recursion tree
// Space    : O(n) - the stack only holds one path at a time (depth is at most n)
// Trade-off: This problem has OPTIMAL SUBSTRUCTURE and OVERLAPPING SUBPROBLEMS - the same remaining
//            lengths get re-solved many times - so it is a natural dynamic-programming candidate.
//            Memoizing on n collapses it to O(n) time (compare DynamicProgramming/ for that pattern).

public class ropeCutting {
    public static void main(String[] args) {
        System.out.println(maxCuts(23, 12, 9, 11)); // expected: 2  (11 + 12)
        System.out.println(maxCuts(5, 2, 5, 1));    // expected: 5  (1+1+1+1+1 beats a single 5)
        System.out.println(maxCuts(7, 5, 5, 5));    // expected: -1 (7 cannot be built from 5s)
    }

    static int maxCuts(int n, int a, int b, int c) {
        // BASE CASE 1: the rope was consumed EXACTLY - a valid cutting, contributing 0 further cuts.
        if (n == 0) {
            return 0;
        }
        // BASE CASE 2: we overshot, so this branch of choices is invalid. -1 is the "impossible"
        // sentinel that must be propagated, never counted.
        if (n < 0) {
            return -1;
        }

        // Try each allowed cut length and keep whichever leads to the most pieces.
        int res = Math.max(
                      Math.max(maxCuts(n - a, a, b, c),
                               maxCuts(n - b, a, b, c)),
                               maxCuts(n - c, a, b, c));

        // If EVERY branch was impossible, this length is impossible too - pass the sentinel up
        // unchanged. Adding 1 to -1 here would corrupt it into 0 and wrongly look like success.
        if (res == -1) {
            return -1;
        }
        return res + 1;   // +1 counts the cut made at THIS level
    }
}
