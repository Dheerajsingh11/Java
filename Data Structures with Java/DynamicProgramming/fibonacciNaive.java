// Problem  : Compute the n-th Fibonacci number  F(n) = F(n-1) + F(n-2), F(0)=0, F(1)=1.
// Approach : NAIVE - translate the recurrence directly into recursion.
// Intuition: The definition is recursive, so the simplest code mirrors it. But this recomputes the
//            same subproblems exponentially often (F(n-2) is computed by both F(n-1) and F(n)).
// Time     : O(2^n) - the recursion tree roughly doubles at each level   Space: O(n) call stack
// Trade-off: Beautiful and wrong-for-scale: F(50) already does ~10^10 calls. This is the textbook
//            motivation for DP - the Medium version caches results, the Efficient one drops the array.

public class fibonacciNaive {

    static long fib(int n) {
        if (n < 2) return n;               // base cases F(0)=0, F(1)=1
        return fib(n - 1) + fib(n - 2);    // same subproblems recomputed again and again
    }

    public static void main(String[] args) {
        for (int n = 0; n <= 10; n++) System.out.print(fib(n) + " ");
        System.out.println(); // 0 1 1 2 3 5 8 13 21 34 55
        System.out.println("fib(30) = " + fib(30)); // 832040 (already noticeably slow)
    }
}
