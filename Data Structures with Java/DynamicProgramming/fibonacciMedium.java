// Problem  : n-th Fibonacci number.
// Approach : MEDIUM - top-down MEMOIZATION: same recursion, but cache each computed F(k) so it is
//            only ever computed once.
// Intuition: The naive tree repeats subproblems; a memo table turns each repeat into an O(1) lookup.
//            This is the essence of dynamic programming - "recursion + remembering".
// Time     : O(n) - each of F(0)..F(n) computed once   Space: O(n) memo + O(n) recursion stack
// Trade-off: Exponential -> linear with a tiny cache. Keeps the natural recursive shape (easy to
//            derive), at the cost of stack depth. The Efficient version removes both the recursion
//            and the full array.

public class fibonacciMedium {

    static long fib(int n, long[] memo) {
        if (n < 2) return n;               // base cases
        if (memo[n] != -1) return memo[n]; // already computed? return the cached value
        memo[n] = fib(n - 1, memo) + fib(n - 2, memo); // compute once, then store
        return memo[n];
    }

    static long fib(int n) {
        long[] memo = new long[n + 1];
        java.util.Arrays.fill(memo, -1);   // -1 marks "not computed yet" (F is never negative)
        return fib(n, memo);
    }

    public static void main(String[] args) {
        System.out.println("fib(30) = " + fib(30)); // 832040 (now instant)
        System.out.println("fib(90) = " + fib(90)); // 2880067194370816120 (fits in long)
    }
}
