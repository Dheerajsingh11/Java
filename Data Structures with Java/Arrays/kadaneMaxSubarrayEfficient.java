// Problem  : Maximum sum of a contiguous subarray (Kadane's algorithm).
// Approach : EFFICIENT - one pass tracking the best subarray sum ENDING at the current index; extend
//            it or restart from the current element, whichever is larger.
// Intuition: The best subarray ending at i either extends the best ending at i-1 (if that was
//            positive help) or starts fresh at a[i]. So curr = max(a[i], curr + a[i]). The global
//            answer is the max curr over all i. Carrying a negative prefix forward can only hurt.
// Time     : O(n) - a single pass   Space: O(1)
// Trade-off: Optimal. The subtlety is initialization: seed with the first element (not 0) so
//            all-negative arrays return their largest element rather than an impossible empty sum.

public class kadaneMaxSubarrayEfficient {

    static int maxSubarray(int[] a) {
        int curr = a[0];  // best sum of a subarray ENDING at index 0 is a[0] itself
        int best = a[0];  // best seen anywhere so far
        for (int i = 1; i < a.length; i++) {
            // Either extend the previous best-ending-here, or drop it and start at a[i].
            curr = Math.max(a[i], curr + a[i]);
            best = Math.max(best, curr);          // update the global answer
        }
        return best;
    }

    // Variant that also reports the subarray bounds (handy for understanding the walk).
    static void maxSubarrayWithBounds(int[] a) {
        int curr = a[0], best = a[0], start = 0, bestL = 0, bestR = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] > curr + a[i]) { curr = a[i]; start = i; } // restart -> new window begins here
            else curr = curr + a[i];                            // extend the current window
            if (curr > best) { best = curr; bestL = start; bestR = i; }
        }
        System.out.println("max sum " + best + " over indices [" + bestL + ", " + bestR + "]");
    }

    public static void main(String[] args) {
        int[] a = { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        System.out.println(maxSubarray(a));   // 6
        maxSubarrayWithBounds(a);             // max sum 6 over indices [3, 6]
        int[] b = { -5, -2, -8 };
        System.out.println(maxSubarray(b));   // -2 (all-negative handled by seeding with a[0])
    }
}
