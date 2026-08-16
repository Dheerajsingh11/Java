package Questions;

// Problem  : Find the minimum ABSOLUTE difference between any two elements of an array.
// Approach : EFFICIENT - sort the array, then compare only ADJACENT pairs.
// Intuition: The crucial insight: once sorted, the two closest values MUST be next to each other.
//            If some non-adjacent pair (i, j) were closest, every element between them would be even
//            closer to one of the two - a contradiction. So n-1 adjacent comparisons suffice, and
//            the O(n^2) pair scan collapses to O(n).
// Time     : O(n log n) - dominated by the sort; the scan itself is only O(n)
// Space    : O(1) extra (Arrays.sort on primitives sorts in place)
// Trade-off: A classic example of SORTING AS A PREPROCESSING STEP - paying n log n up front to make
//            the actual problem linear and trivial. Note it MUTATES the caller's array; clone first
//            if the original order matters.

import java.util.Arrays;

public class minDiffEfficient {
    public static void main(String[] args) {
        int arr[] = { 1, 5, 3, 19, 18, 25 };
        System.out.println("Minimum difference is: " + minDiff(arr)); // expected: 1 (19 and 18)
    }

    static int minDiff(int arr[]) {
        int n = arr.length;

        // Sorting brings every potentially-closest pair into adjacency. This is the entire trick.
        Arrays.sort(arr);

        int min_diff = Integer.MAX_VALUE;
        for (int i = 1; i < n; i++) {
            // No Math.abs needed - the array is ascending, so arr[i] >= arr[i-1] and the
            // difference is already non-negative.
            min_diff = Math.min(min_diff, arr[i] - arr[i - 1]);
        }
        return min_diff;
    }
}
