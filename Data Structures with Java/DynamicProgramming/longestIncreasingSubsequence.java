// Problem  : Length of the Longest Increasing Subsequence (LIS) - the longest strictly increasing
//            subsequence (not necessarily contiguous).
// Approach : Two tiers in one file. MEDIUM: O(n^2) DP where dp[i] = LIS ending at i. EFFICIENT:
//            O(n log n) patience method using binary search over "tails".
// Intuition (DP)     : the LIS ending at i extends the best LIS ending at any earlier smaller element.
// Intuition (n log n): keep tails[k] = smallest possible tail of an increasing subsequence of length
//            k+1. Each new value either extends the longest chain or improves (lowers) an existing
//            tail via binary search. The length of 'tails' is the LIS length.
// Time     : dp O(n^2); efficient O(n log n)   Space: O(n)
// Trade-off: The O(n^2) DP is easy and also reconstructs the sequence; the O(n log n) version is
//            faster for large n but only gives the LENGTH easily (reconstruction needs extra work).

import java.util.ArrayList;
import java.util.List;

public class longestIncreasingSubsequence {

    // ---------- MEDIUM: O(n^2) DP ----------
    static int lisDP(int[] a) {
        int n = a.length;
        if (n == 0) return 0;
        int[] dp = new int[n];                 // dp[i] = length of LIS ending exactly at i
        int best = 1;
        for (int i = 0; i < n; i++) {
            dp[i] = 1;                         // the element alone is a subsequence of length 1
            for (int j = 0; j < i; j++) {
                if (a[j] < a[i]) dp[i] = Math.max(dp[i], dp[j] + 1); // extend a smaller-ending chain
            }
            best = Math.max(best, dp[i]);
        }
        return best;
    }

    // ---------- EFFICIENT: O(n log n) patience sorting ----------
    static int lisFast(int[] a) {
        List<Integer> tails = new ArrayList<>(); // tails.get(k) = smallest tail of an LIS of length k+1
        for (int x : a) {
            // Binary search for the first tail >= x (lower bound) to keep it strictly increasing.
            int lo = 0, hi = tails.size();
            while (lo < hi) {
                int mid = (lo + hi) / 2;
                if (tails.get(mid) < x) lo = mid + 1;
                else hi = mid;
            }
            if (lo == tails.size()) tails.add(x);   // x extends the longest chain
            else tails.set(lo, x);                  // x lowers an existing tail (more room to grow later)
        }
        return tails.size();
    }

    public static void main(String[] args) {
        int[] a = { 10, 9, 2, 5, 3, 7, 101, 18 };
        System.out.println("DP   : " + lisDP(a));   // 4  (e.g. 2,3,7,18 or 2,3,7,101)
        System.out.println("fast : " + lisFast(a)); // 4
    }
}
