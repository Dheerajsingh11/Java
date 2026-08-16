// Problem  : 0/1 Knapsack - maximize value within weight capacity W.
// Approach : MEDIUM - top-down MEMOIZATION on the state (item index i, remaining capacity cap).
// Intuition: The naive recursion revisits the same (i, cap) pairs many times. There are only
//            n * (W+1) such states, so caching each one turns exponential work into a table fill.
// Time     : O(n * W)   Space: O(n * W) memo + O(n) recursion stack
// Trade-off: "Pseudo-polynomial" - polynomial in n and W but exponential in the number of BITS of W.
//            Huge win over naive; the Efficient tabulation removes recursion and can shrink space.

import java.util.Arrays;

public class knapsack01Medium {

    static int solve(int[] wt, int[] val, int i, int cap, int[][] memo) {
        if (i == wt.length || cap == 0) return 0;
        if (memo[i][cap] != -1) return memo[i][cap]; // reuse a previously solved state

        int exclude = solve(wt, val, i + 1, cap, memo);
        int include = 0;
        if (wt[i] <= cap) include = val[i] + solve(wt, val, i + 1, cap - wt[i], memo);

        return memo[i][cap] = Math.max(include, exclude); // store then return
    }

    static int knapsack(int[] wt, int[] val, int W) {
        int[][] memo = new int[wt.length][W + 1];
        for (int[] row : memo) Arrays.fill(row, -1); // -1 = state not computed yet
        return solve(wt, val, 0, W, memo);
    }

    public static void main(String[] args) {
        int[] wt = { 1, 3, 4, 5 };
        int[] val = { 1, 4, 5, 7 };
        System.out.println(knapsack(wt, val, 7)); // 9
    }
}
