// Problem  : Return the n-th Fibonacci number, where F(0)=0, F(1)=1, F(n)=F(n-1)+F(n-2).
// Approach : NAIVE recursion - translate the mathematical recurrence directly into two recursive calls.
// Intuition: The definition is itself recursive, so the code mirrors it exactly. The catch is that
//            the two calls RE-SOLVE the same subproblems over and over: computing F(5) computes F(3)
//            twice, F(2) three times, and so on - the work explodes.
// Time     : O(2^n) - the call tree branches in two at nearly every level
// Space    : O(n) - the stack only holds one root-to-leaf path at a time, not the whole tree
// Trade-off: This is THE textbook motivation for dynamic programming. Because the subproblems
//            OVERLAP, caching them collapses the cost from exponential to linear. See
//            DynamicProgramming/fibonacci{Naive,Medium,Efficient}.java for the full three-tier
//            progression: this brute force -> memoization O(n) -> tabulation with O(1) space.

public class fibonacci {
    public static void main(String[] args) {
        int n = 5;
        System.out.println(fib(n));   // expected: 5   (0,1,1,2,3,5)
        System.out.println(fib(10));  // expected: 55
        // Try fib(45) to FEEL the exponential blow-up - it takes seconds, while the DP version
        // in DynamicProgramming/ answers instantly.
    }

    static int fib(int n) {
        // BASE CASE 1: F(0) = 0
        if (n == 0) {
            return 0;
        }
        // BASE CASE 2: F(1) = 1
        // BOTH base cases are required. With only one, the n-2 branch would skip past the stopping
        // point into negative n and recurse forever -> StackOverflowError.
        if (n == 1) {
            return 1;
        }
        // RECURSIVE CASE: the definition itself. Each call spawns TWO more calls, which is exactly
        // why the total work is exponential rather than linear.
        return fib(n - 1) + fib(n - 2);
    }
    // Why O(2^n) concretely: fib(n) calls fib(n-1) and fib(n-2); the number of calls follows the
    // Fibonacci sequence itself, which grows like phi^n (phi ~ 1.618). Every repeated subtree is
    // wasted work that memoization eliminates.
}
