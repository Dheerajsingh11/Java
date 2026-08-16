package Questions;

// Problem  : Find the minimum ABSOLUTE difference between any two elements of an array.
// Approach : NAIVE - compare every pair and keep the smallest absolute difference.
// Intuition: The closest pair could be anywhere, so brute force examines all n(n-1)/2 pairs. No
//            ordering is assumed or exploited.
// Time     : O(n^2)   Space: O(1)
// Trade-off: Works on unsorted input with no extra memory, but quadratic. The key insight the
//            EFFICIENT version uses: after SORTING, the closest pair must be ADJACENT, so only n-1
//            comparisons are needed - reducing the whole problem to O(n log n) sorting cost.

public class minDiffNaive {
    public static void main(String[] args) {
        int[] arr = { 1, 3, 6, 10, 15 };
        System.out.println(minDiff(arr));                         // expected: 2  (3 and 1)
        System.out.println(minDiff(new int[]{ 5, 5, 9 }));         // expected: 0  (equal pair)
    }

    static int minDiff(int[] arr) {
        int res = Integer.MAX_VALUE;   // seed with "infinity" so any real difference beats it

        for (int i = 0; i < arr.length; i++) {
            // j < i compares each unordered pair exactly ONCE (avoids re-checking (j,i) after (i,j)
            // and never compares an element with itself, which would give a bogus difference of 0).
            for (int j = 0; j < i; j++) {
                res = Math.min(res, Math.abs(arr[i] - arr[j]));
            }
        }
        return res;
        // Edge: an array of fewer than 2 elements has no pair; res stays Integer.MAX_VALUE.
        // Overflow note: Math.abs on two extreme ints can overflow - use long for adversarial input.
    }
}
