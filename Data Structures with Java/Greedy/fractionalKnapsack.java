// Problem  : Fractional knapsack - maximize value in a capacity-W bag, where items MAY be broken into
//            fractions.
// Approach : GREEDY - sort items by value-to-weight RATIO (density) descending and fill greedily,
//            taking a fraction of the last item if it does not fully fit.
// Intuition: Every unit of capacity should hold the most valuable material available, so take the
//            densest items first. Because we can split items, the bag is always filled exactly.
// Time     : O(n log n) for the sort   Space: O(1)
// Trade-off: Greedy is OPTIMAL for the FRACTIONAL version (unlike 0/1 knapsack, which needs DP - see
//            DynamicProgramming/). The ability to take fractions is exactly what makes greedy work.

import java.util.Arrays;

public class fractionalKnapsack {

    static double maxValue(int[] wt, int[] val, int W) {
        int n = wt.length;
        // items[i] = { weight, value }; sort by value/weight ratio descending.
        Integer[] idx = new Integer[n];
        for (int i = 0; i < n; i++) idx[i] = i;
        Arrays.sort(idx, (a, b) -> Double.compare((double) val[b] / wt[b], (double) val[a] / wt[a]));

        double total = 0;
        int cap = W;
        for (int i : idx) {
            if (cap == 0) break;
            if (wt[i] <= cap) {
                total += val[i];        // take the whole item
                cap -= wt[i];
            } else {
                total += val[i] * ((double) cap / wt[i]); // take the fraction that fits
                cap = 0;                                   // bag is now full
            }
        }
        return total;
    }

    public static void main(String[] args) {
        int[] wt  = { 10, 20, 30 };
        int[] val = { 60, 100, 120 };
        System.out.println(maxValue(wt, val, 50)); // 240.0  (item1 + item2 + 2/3 of item3)
    }
}
