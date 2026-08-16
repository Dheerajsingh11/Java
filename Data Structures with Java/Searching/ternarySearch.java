// Problem  : Find a target in a SORTED array by splitting into THREE parts each step (variant of
//            binary search).
// Approach : Two midpoints divide the range into thirds; compare the target to both to discard two
//            thirds each iteration.
// Intuition: Like binary search, but with two cut points. Each step keeps only the third that can
//            contain the target.
// Time     : O(log3 n) iterations, but ~2 comparisons each -> more comparisons than binary search
// Space    : O(1) iterative
// Trade-off: Despite fewer iterations, ternary search does MORE comparisons overall than binary
//            search, so for plain sorted lookup binary search wins. Ternary search shines on
//            UNIMODAL functions (finding a peak/valley), not on sorted-array membership.

public class ternarySearch {

    static int search(int[] a, int target) {
        int lo = 0, hi = a.length - 1;
        while (lo <= hi) {
            int third = (hi - lo) / 3;
            int m1 = lo + third;        // first cut point
            int m2 = hi - third;        // second cut point

            if (a[m1] == target) return m1;
            if (a[m2] == target) return m2;

            if (target < a[m1]) {
                hi = m1 - 1;            // target is in the first third
            } else if (target > a[m2]) {
                lo = m2 + 1;           // target is in the last third
            } else {
                lo = m1 + 1;           // target is in the middle third
                hi = m2 - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] a = { 1, 3, 5, 7, 9, 11, 13, 15, 17 };
        System.out.println(search(a, 9));  // 4
        System.out.println(search(a, 17)); // 8
        System.out.println(search(a, 4));  // -1
    }
}
