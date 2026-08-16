// Problem  : 0/1 Knapsack - pick a subset of items (each taken or not) to maximize value without
//            exceeding a weight capacity W.
// Approach : NAIVE - for each item, try BOTH choices (include / exclude) via recursion; return the
//            better result.
// Intuition: The optimal packing either uses the current item (if it fits) or it does not. Exploring
//            both branches for every item covers all 2^n subsets.
// Time     : O(2^n) - two branches per item   Space: O(n) recursion depth
// Trade-off: Correct but exponential. Because different branches revisit the SAME (index, capacity)
//            states, memoization (Medium) collapses it to pseudo-polynomial time.

public class knapsack01Naive {

    // Consider items from index i onward with 'cap' capacity remaining.
    static int solve(int[] wt, int[] val, int i, int cap) {
        if (i == wt.length || cap == 0) return 0;    // no items left or no room -> value 0

        // Option A: skip item i.
        int exclude = solve(wt, val, i + 1, cap);

        // Option B: take item i, only if it fits; add its value and reduce capacity.
        int include = 0;
        if (wt[i] <= cap) {
            include = val[i] + solve(wt, val, i + 1, cap - wt[i]);
        }
        return Math.max(include, exclude);           // best of the two choices
    }

    public static void main(String[] args) {
        int[] wt = { 1, 3, 4, 5 };
        int[] val = { 1, 4, 5, 7 };
        int W = 7;
        System.out.println(solve(wt, val, 0, W)); // 9  (items with weight 3 and 4 -> value 4+5)
    }
}
