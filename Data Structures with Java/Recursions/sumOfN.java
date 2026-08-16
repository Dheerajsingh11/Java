// Problem  : Compute the sum of the first N natural numbers (1 + 2 + ... + N) recursively.
// Approach : sum(n) = n + sum(n-1), with sum(0) = 0 as the base case.
// Intuition: The total of 1..n is just "n itself plus the total of everything below it". That
//            self-referential definition IS the recursion - each call peels off one number and
//            delegates the smaller sum downward.
// Time     : THETA(n) - the recurrence T(n) = T(n-1) + THETA(1) unrolls into n constant-time steps
// Space    : THETA(n) - n frames stack up before any of them can return
// Trade-off: Recursion here is illustrative, not optimal. There is a closed-form formula
//            n*(n+1)/2 that answers this in O(1) time and O(1) space - always prefer it in practice.
//            An iterative loop would also be O(n) time but O(1) space. Use this file to understand
//            how a recurrence maps to code, not as the way to sum numbers.

public class sumOfN {
    public static void main(String[] args) {
        int n = 5;
        System.out.println("Recursive : " + sumN(n));   // expected: 15
        System.out.println("Formula   : " + sumFormula(n)); // expected: 15 (same answer, O(1))
    }

    static int sumN(int n) {
        // BASE CASE: the sum of zero numbers is 0. This both answers the smallest case and
        // terminates the recursion.
        if (n == 0) {
            return 0;
        }
        // RECURSIVE CASE: add the current number to the sum of everything beneath it.
        // The addition happens while UNWINDING - each frame waits for its child's result.
        return n + sumN(n - 1);
    }

    // Gauss's closed form - the same result with no loop and no recursion at all.
    static int sumFormula(int n) {
        return n * (n + 1) / 2;    // O(1) time, O(1) space
    }
    // Edge: for large n the sum can overflow int (n around 65,536 already exceeds it); use long.
}
