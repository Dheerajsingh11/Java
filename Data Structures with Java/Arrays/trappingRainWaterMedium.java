// Problem  : Water trapped between bars.
// Approach : MEDIUM - precompute, for every index, the tallest wall to its left and to its right in
//            two passes, then sum the water per index in a third pass.
// Intuition: The naive version recomputes the same maxima over and over. Caching leftMax[i] and
//            rightMax[i] once turns each per-index lookup into O(1).
// Time     : O(n) time   Space: O(n) for the two prefix/suffix-max arrays
// Trade-off: Big speedup over naive, at the cost of two extra arrays. The Efficient two-pointer
//            version reaches the same O(n) time with O(1) space.

public class trappingRainWaterMedium {

    static int trap(int[] h) {
        int n = h.length;
        if (n == 0) return 0;
        int[] leftMax = new int[n];   // leftMax[i]  = tallest bar in h[0..i]
        int[] rightMax = new int[n];  // rightMax[i] = tallest bar in h[i..n-1]

        leftMax[0] = h[0];
        for (int i = 1; i < n; i++) leftMax[i] = Math.max(leftMax[i - 1], h[i]);   // prefix maxima

        rightMax[n - 1] = h[n - 1];
        for (int i = n - 2; i >= 0; i--) rightMax[i] = Math.max(rightMax[i + 1], h[i]); // suffix maxima

        int total = 0;
        for (int i = 0; i < n; i++) {
            total += Math.min(leftMax[i], rightMax[i]) - h[i]; // water above bar i
        }
        return total;
    }

    public static void main(String[] args) {
        int[] h = { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 };
        System.out.println(trap(h)); // 6
    }
}
