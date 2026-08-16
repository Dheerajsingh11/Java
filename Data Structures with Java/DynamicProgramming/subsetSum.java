// Problem  : Can any subset of the array sum to exactly the target?
// Approach : Three tiers. NAIVE tries every subset by recursion (O(2^n)). MEDIUM memoizes on
//            (index, remaining) (O(n*target)). EFFICIENT is a 1-D boolean DP swept backwards.
// Intuition: Each element is a binary choice: include it or not. That gives 2^n subsets, but the
//            recursion only ever cares about (which elements remain, how much sum remains) - and
//            there are just n * target such states, so the vast majority of the tree is repeated work.
// Time     : naive O(2^n); medium and efficient O(n * target)   Space: O(n*target) -> O(target)
// Trade-off: This is the decision version of 0/1 knapsack with value == weight, so the same
//            backwards-sweep rule applies. Note the complexity is PSEUDO-POLYNOMIAL: it is linear in
//            the target's VALUE but exponential in the number of BITS needed to write it, which is
//            why subset sum remains NP-complete despite this DP.

import java.util.Arrays;

public class subsetSum {

    // ---------- NAIVE: include/exclude recursion ----------
    static boolean naive(int[] a, int i, int remaining) {
        if (remaining == 0) return true;          // built the target exactly
        if (i == a.length || remaining < 0) return false;
        return naive(a, i + 1, remaining - a[i])  // include a[i]
            || naive(a, i + 1, remaining);        // exclude a[i]
    }

    // ---------- MEDIUM: memoize the (index, remaining) state ----------
    static Boolean[][] memo;
    static boolean medium(int[] a, int i, int remaining) {
        if (remaining == 0) return true;
        if (i == a.length || remaining < 0) return false;
        if (memo[i][remaining] != null) return memo[i][remaining];
        return memo[i][remaining] = medium(a, i + 1, remaining - a[i])
                                 || medium(a, i + 1, remaining);
    }

    // ---------- EFFICIENT: 1-D bottom-up ----------
    static boolean efficient(int[] a, int target) {
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;                 // a sum of 0 is always reachable - take nothing

        for (int num : a) {
            // SWEEP DOWNWARD. dp[s - num] must still refer to the state BEFORE this element was
            // considered. Going upward would let dp[s] be built from a dp[s - num] that already
            // used `num`, i.e. reusing the same element more than once - that is the UNBOUNDED
            // variant, not 0/1. This single loop direction is the whole difference.
            for (int s = target; s >= num; s--) {
                if (dp[s - num]) dp[s] = true;
            }
        }
        return dp[target];
    }

    // Variant worth knowing: which subset achieved it (reconstruct from a 2-D table).
    static boolean canPartitionEqualHalves(int[] a) {
        int total = Arrays.stream(a).sum();
        if (total % 2 != 0) return false;          // an odd total can never split evenly
        return efficient(a, total / 2);            // each half must sum to total/2
    }

    public static void main(String[] args) {
        int[] a = { 3, 34, 4, 12, 5, 2 };

        memo = new Boolean[a.length][10];
        System.out.println("target 9  naive/medium/efficient : "
                + naive(a, 0, 9) + " / " + medium(a, 0, 9) + " / " + efficient(a, 9));  // true (4+5)

        memo = new Boolean[a.length][31];
        System.out.println("target 30 naive/medium/efficient : "
                + naive(a, 0, 30) + " / " + medium(a, 0, 30) + " / " + efficient(a, 30)); // false

        System.out.println("target 0  : " + efficient(a, 0));   // true - the empty subset

        // Equal-partition: a classic direct application.
        System.out.println("{1,5,11,5} splits evenly : " + canPartitionEqualHalves(new int[]{1,5,11,5}));
        System.out.println("{1,2,3,5}  splits evenly : " + canPartitionEqualHalves(new int[]{1,2,3,5}));
    }
}

/* ------------------------- RELATIONSHIP TO KNAPSACK -------------------------
 * Subset sum IS 0/1 knapsack with value == weight and the question changed from "maximize value"
 * to "can we hit exactly W". The identical backwards-sweep requirement appears in both - see
 * knapsack01Efficient.java, where the same loop direction prevents reusing an item.
 *
 * Direct applications:
 *   - Partition into two equal halves (above)
 *   - Splitting a bill or workload evenly
 *   - "Can these transactions account for exactly this discrepancy?"
 *   - Minimum-difference partition (find the reachable sum closest to total/2)
 * ---------------------------------------------------------------------------- */
