// Problem  : 0/1 Knapsack - maximize value within weight capacity W.
// Approach : EFFICIENT - bottom-up TABULATION, then SPACE-OPTIMIZED to a single 1-D array.
// Intuition: dp[c] = best value achievable with capacity c. Processing items one at a time and
//            sweeping capacity from HIGH to LOW ensures each item is used at most once (0/1, not
//            unbounded) - a lower capacity must still reflect the PREVIOUS item's row.
// Time     : O(n * W)   Space: O(W) for the 1-D version (O(n*W) for the 2-D table)
// Trade-off: Same time as memoization but no recursion (no stack limit) and minimal memory. The
//            reverse capacity loop is the subtle detail that enforces the 0/1 rule.

public class knapsack01Efficient {

    // 2-D tabulation (clearest to read). dp[i][c] = best using first i items with capacity c.
    static int knapsackTable(int[] wt, int[] val, int W) {
        int n = wt.length;
        int[][] dp = new int[n + 1][W + 1];
        for (int i = 1; i <= n; i++) {
            for (int c = 0; c <= W; c++) {
                dp[i][c] = dp[i - 1][c];                     // exclude item i-1
                if (wt[i - 1] <= c) {                        // include it if it fits
                    dp[i][c] = Math.max(dp[i][c], val[i - 1] + dp[i - 1][c - wt[i - 1]]);
                }
            }
        }
        return dp[n][W];
    }

    // 1-D space-optimized: reuse a single row, sweeping capacity DOWNWARD.
    static int knapsack(int[] wt, int[] val, int W) {
        int[] dp = new int[W + 1];
        for (int i = 0; i < wt.length; i++) {
            for (int c = W; c >= wt[i]; c--) {               // HIGH->LOW so item i is counted once
                dp[c] = Math.max(dp[c], val[i] + dp[c - wt[i]]);
            }
        }
        return dp[W];
    }

    public static void main(String[] args) {
        int[] wt = { 1, 3, 4, 5 };
        int[] val = { 1, 4, 5, 7 };
        System.out.println("table : " + knapsackTable(wt, val, 7)); // 9
        System.out.println("1-D   : " + knapsack(wt, val, 7));      // 9
    }
}
