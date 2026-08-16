// Problem  : Find the length of the LONGEST subarray containing an equal number of 0s and 1s.
// Approach : NAIVE - check every subarray (i, j), counting zeroes and ones as the window extends.
// Intuition: For each start index i, extend the end index j one step at a time and maintain running
//            counts. Whenever the two counts are equal, that window qualifies, so record its length
//            if it beats the best so far.
// Time     : THETA(n^2) - n starting positions, each extended up to n times (counts update in O(1))
// Space    : THETA(1) - only counters, no extra structures
// Trade-off: There is a much better O(n) HASHING solution: treat 0 as -1, take a running prefix sum,
//            and note that any two indices with the SAME prefix sum bracket a balanced subarray - so
//            a HashMap of first-seen prefix sums answers it in one pass. See
//            Hashing/longestSubArrEfficient.java and Hashing/zeroSumArrEfficient.java for that idea.
// NOTE     : Despite living in the Recursions folder, this solution is ITERATIVE - it is kept here
//            alongside the related practice problems.

public class equalOnes {
    public static void main(String[] args) {
        int[] arr = { 1, 1, 0, 1, 0, 0, 1 };
        System.out.println(longSubArr(arr, arr.length)); // expected: 6  (subarray 1,0,1,0,0,1)
        System.out.println(longSubArr(new int[]{ 1, 1, 1 }, 3)); // expected: 0 (never balances)
    }

    static int longSubArr(int arr[], int n) {
        int res = 0;                       // best balanced length found so far

        for (int i = 0; i < n; i++) {      // every possible START of a subarray
            int count0 = 0, count1 = 0;    // counts are reset for each new start

            for (int j = i; j < n; j++) {  // extend the END one element at a time
                // Update in O(1) by only accounting for the newly added element, instead of
                // recounting the whole window (which would make this O(n^3)).
                if (arr[j] == 0) count0++;
                else             count1++;

                // Balanced window found - its length is j - i + 1 (inclusive on both ends).
                if (count0 == count1) {
                    res = Math.max(res, j - i + 1);
                }
            }
        }
        return res;                        // 0 means no balanced subarray exists
    }
}
