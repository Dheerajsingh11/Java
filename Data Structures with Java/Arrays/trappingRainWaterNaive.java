// Problem  : Given bar heights, compute how much rain water is trapped between the bars.
// Approach : NAIVE - for each bar, scan left and right to find the tallest wall on each side; water
//            above this bar = min(leftMax, rightMax) - height[i].
// Intuition: Water sits on a bar up to the shorter of the two tallest walls surrounding it. The
//            water above a single position depends only on those two maxima and its own height.
// Time     : O(n^2) - each bar scans the whole array for its two maxima   Space: O(1)
// Trade-off: Direct and easy to reason about, but quadratic. The Medium version precomputes the
//            maxima (O(n) time, O(n) space); the Efficient version uses two pointers (O(n)/O(1)).

public class trappingRainWaterNaive {

    static int trap(int[] h) {
        int n = h.length, total = 0;
        for (int i = 0; i < n; i++) {
            int leftMax = 0, rightMax = 0;
            for (int l = 0; l <= i; l++) leftMax = Math.max(leftMax, h[l]);   // tallest wall to the left (incl i)
            for (int r = i; r < n; r++)  rightMax = Math.max(rightMax, h[r]);  // tallest wall to the right (incl i)
            total += Math.min(leftMax, rightMax) - h[i]; // water level minus the bar itself
        }
        return total;
    }

    public static void main(String[] args) {
        int[] h = { 0, 1, 0, 2, 1, 0, 1, 3, 2, 1, 2, 1 };
        System.out.println(trap(h)); // 6
        int[] h2 = { 4, 2, 0, 3, 2, 5 };
        System.out.println(trap(h2)); // 9
    }
}
