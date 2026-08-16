// Problem  : Find the LENGTH of the longest subarray whose elements sum to a given value.
// Approach : NAIVE - try every start index, extend the end with a running sum, and record the length
//            whenever the target is hit.
// Intuition: Unlike the "does it exist?" variant, we must not stop at the first match - a LONGER
//            qualifying subarray may appear later, so every window is examined and the best kept.
// Time     : O(n^2) - n starts, each extended up to n times
// Space    : O(1)
// Trade-off: Simple and memory-free, but quadratic. The EFFICIENT version reaches O(n) using prefix
//            sums plus a HashMap that stores the FIRST index at which each prefix sum occurred -
//            "first" being exactly what maximizes the resulting length.

public class longestSubArrNaive {
    public static void main(String[] args) {
        int arr[] = { 1, 1, 2, 3, 4, 5, 3, 7 };
        System.out.println(subArray(arr, 10)); // expected: 4  (1+2+3+4)
        System.out.println(subArray(arr, 100)); // expected: 0 (no such subarray)
    }

    // Returns the longest qualifying length, or 0 if none exists.
    static int subArray(int[] arr, int sum) {
        int res = 0;                              // best length found so far

        for (int i = 0; i < arr.length; i++) {    // every START index
            int currSum = 0;

            for (int j = i; j < arr.length; j++) { // extend the END
                currSum += arr[j];

                if (currSum == sum) {
                    // Do NOT return here - keep scanning, since a longer match may exist further on.
                    res = Math.max(res, j - i + 1);
                }
            }
        }
        return res;
    }
}
