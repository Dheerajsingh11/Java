// Problem  : Water trapped between bars.
// Approach : EFFICIENT - two pointers moving inward, tracking leftMax and rightMax on the fly, so no
//            precomputed arrays are needed.
// Intuition: Whichever side currently has the SHORTER running max bounds the water there - we can
//            settle that side immediately because the taller opposite wall guarantees containment.
//            So we always advance the shorter side and add its trapped water.
// Time     : O(n) single pass   Space: O(1)
// Trade-off: Optimal in both time and space - the standard interview answer. The insight (advance the
//            side with the smaller max) is less obvious than the Medium prefix/suffix approach.

public class trappingRainWaterEfficient {

    static int trap(int[] h) {
        int left = 0, right = h.length - 1;
        int leftMax = 0, rightMax = 0, total = 0;

        while (left < right) {
            if (h[left] < h[right]) {
                // Left bar is shorter, so the right side has a wall >= h[left]: water on the left is
                // bounded by leftMax alone.
                if (h[left] >= leftMax) leftMax = h[left]; // new left wall, no water here
                else total += leftMax - h[left];           // trapped above this bar
                left++;
            } else {
                // Symmetric: right bar is the shorter side.
                if (h[right] >= rightMax) rightMax = h[right];
                else total += rightMax - h[right];
                right--;
            }
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
