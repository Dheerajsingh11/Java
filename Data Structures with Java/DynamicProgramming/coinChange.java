// Problem  : Fewest coins from given denominations that sum to a target amount (unlimited coins per
//            denomination). Return -1 if impossible.
// Approach : Bottom-up tabulation. dp[x] = min coins to make amount x; try every coin for each amount.
// Intuition: To make amount x optimally, pick some coin c (c <= x), then make x-c optimally. So
//            dp[x] = 1 + min over coins c of dp[x - c]. Building x from 0 upward means dp[x-c] is
//            already final when we need it.
// Time     : O(amount * numCoins)   Space: O(amount)
// Trade-off: The naive "try all combinations" recursion is exponential; this DP is polynomial. This
//            is the MIN-COINS variant (unbounded coins) - different from the 0/1 knapsack shape.

import java.util.Arrays;

public class coinChange {

    static int minCoins(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);   // sentinel "infinity" (any real answer is <= amount)
        dp[0] = 0;                     // zero coins make amount 0

        for (int x = 1; x <= amount; x++) {
            for (int c : coins) {
                if (c <= x && dp[x - c] + 1 < dp[x]) {
                    dp[x] = dp[x - c] + 1;      // using coin c improves the count for amount x
                }
            }
        }
        // If dp[amount] never improved past the sentinel, the amount is unreachable.
        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        System.out.println(minCoins(new int[]{ 1, 2, 5 }, 11)); // 3  (5 + 5 + 1)
        System.out.println(minCoins(new int[]{ 2 }, 3));        // -1 (odd amount, only 2s)
        System.out.println(minCoins(new int[]{ 1, 3, 4 }, 6));  // 2  (3 + 3, better than 4+1+1)
    }
}
