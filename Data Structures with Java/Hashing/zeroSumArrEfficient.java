// Problem  : Does the array contain a SUBARRAY whose elements sum to zero?
// Approach : EFFICIENT - track the running PREFIX SUM and remember every prefix sum seen in a HashSet.
//            If the same prefix sum ever repeats, the elements between those two points sum to zero.
// Intuition: Let P(i) be the sum of the first i elements. The sum of the subarray (i, j] equals
//            P(j) - P(i). So that subarray sums to ZERO exactly when P(j) == P(i) - i.e. when a
//            prefix sum REPEATS. Detecting a repeat is what the set is for.
// Time     : O(n) - one pass, O(1) average per set operation
// Space    : O(n) - the set may hold one prefix sum per index
// Trade-off: Turns the naive O(n^2) subarray scan into a single pass by spending O(n) memory. This
//            prefix-sum + hashing technique generalizes to "subarray with sum k" (see
//            sumInArrayEfficient.java) and "longest subarray with sum k" (longestSubArrEfficient.java).
// FIX NOTE : The original seeded the set as EMPTY, so it missed subarrays starting at index 0 - for
//            {1,-1} the prefix sum hits 0 but 0 was never in the set, and it wrongly returned false.
//            Pre-loading 0 (the empty prefix) fixes this.

import java.util.HashSet;

public class zeroSumArrEfficient {
    public static void main(String[] args) {
        int arr[] = { 4, 2, -3, 1, 0, 6, 3, -2, 4 };
        System.out.println(subarrayWithZeroSum(arr, arr.length));      // expected: true (e.g. 2,-3,1,0)
        System.out.println(subarrayWithZeroSum(new int[]{ 1, -1 }, 2)); // expected: true (starts at index 0)
        System.out.println(subarrayWithZeroSum(new int[]{ 1, 2, 3 }, 3)); // expected: false
    }

    static boolean subarrayWithZeroSum(int arr[], int n) {
        HashSet<Integer> set = new HashSet<Integer>();

        // Seed with 0 = the sum of the EMPTY prefix. This is what makes subarrays that begin at
        // index 0 detectable: if the running sum returns to 0, it matches this seeded entry.
        set.add(0);

        int pre_sum = 0;
        for (int i = 0; i < n; i++) {
            pre_sum += arr[i];                 // running prefix sum P(i)

            // A repeat means some earlier prefix had the same total, so everything in between
            // contributed a net zero.
            if (set.contains(pre_sum)) {
                return true;
            }
            set.add(pre_sum);
        }
        return false;
    }
    // Note: a single element equal to 0 is also caught, since the prefix sum does not change and
    // therefore repeats the previous value.
}
