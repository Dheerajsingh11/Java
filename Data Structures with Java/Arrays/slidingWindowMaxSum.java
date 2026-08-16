// Problem  : Find the maximum sum of any CONTIGUOUS subarray of a FIXED size k.
// Approach : Two tiers. NAIVE sums each window from scratch (O(n*k)). EFFICIENT slides the window,
//            adding the entering element and subtracting the leaving one (O(n)).
// Intuition: Consecutive windows overlap in k-1 elements. Instead of recomputing that overlap, just
//            update the sum by the single element that enters and the single one that leaves.
// Time     : naive O(n*k); efficient O(n)   Space: O(1)
// Trade-off: The sliding-window pattern is the go-to for fixed-size (and, with two pointers, variable
//            -size) subarray problems, replacing nested loops with a single sweep.

public class slidingWindowMaxSum {

    // ---------- NAIVE ----------
    static int naive(int[] a, int k) {
        int best = Integer.MIN_VALUE;
        for (int i = 0; i + k <= a.length; i++) {
            int sum = 0;
            for (int j = i; j < i + k; j++) sum += a[j]; // recompute the whole window
            best = Math.max(best, sum);
        }
        return best;
    }

    // ---------- EFFICIENT: slide the window ----------
    static int efficient(int[] a, int k) {
        int windowSum = 0;
        for (int i = 0; i < k; i++) windowSum += a[i]; // first window
        int best = windowSum;
        for (int i = k; i < a.length; i++) {
            windowSum += a[i] - a[i - k]; // add the new element, drop the one that left the window
            best = Math.max(best, windowSum);
        }
        return best;
    }

    public static void main(String[] args) {
        int[] a = { 2, 1, 5, 1, 3, 2 };
        int k = 3;
        System.out.println("naive     : " + naive(a, k));     // 9  -> [5, 1, 3]
        System.out.println("efficient : " + efficient(a, k)); // 9
    }
}
