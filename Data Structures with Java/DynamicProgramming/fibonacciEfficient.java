// Problem  : n-th Fibonacci number.
// Approach : EFFICIENT - bottom-up TABULATION, then SPACE-OPTIMIZED to O(1) by keeping only the last
//            two values.
// Intuition: F(n) needs only F(n-1) and F(n-2), so once we build up from the base cases we never
//            need the whole table - two rolling variables suffice.
// Time     : O(n)   Space: O(1)
// Trade-off: Optimal time and space, no recursion (no stack-overflow risk). The only thing lost vs
//            memoization is the natural "just write the recurrence" feel - here we drive it forward
//            by hand. (Even O(log n) is possible via matrix exponentiation - see Math/ folder.)

public class fibonacciEfficient {

    // Full tabulation shown for teaching (an array of all F values).
    static long fibTable(int n) {
        if (n < 2) return n;
        long[] dp = new long[n + 1];
        dp[0] = 0; dp[1] = 1;                       // base cases
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];          // fill forward using already-computed cells
        }
        return dp[n];
    }

    // Space-optimized: only the previous two results are ever needed.
    static long fib(int n) {
        if (n < 2) return n;
        long prev2 = 0, prev1 = 1;                  // F(0), F(1)
        for (int i = 2; i <= n; i++) {
            long curr = prev1 + prev2;              // F(i)
            prev2 = prev1;                          // slide the window forward
            prev1 = curr;
        }
        return prev1;
    }

    public static void main(String[] args) {
        System.out.println("fibTable(10) = " + fibTable(10)); // 55
        System.out.println("fib(90)      = " + fib(90));      // 2880067194370816120
    }
}
