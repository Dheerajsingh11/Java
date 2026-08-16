package Questions;

// Problem  : Count INVERSIONS - pairs (i, j) with i < j but arr[i] > arr[j].
// Approach : NAIVE - check every pair directly.
// Intuition: An inversion is a pair that is "out of order". The count measures how far the array is
//            from being sorted: 0 means already sorted, n(n-1)/2 means exactly reversed. Brute force
//            simply tests all pairs.
// Time     : O(n^2) - n(n-1)/2 comparisons   Space: O(1)
// Trade-off: Fine for small arrays and useful as a correctness reference, but quadratic. The
//            EFFICIENT version piggybacks on MERGE SORT to count in O(n log n) - see
//            totalInversionsEfficient.java for why merging reveals inversions in bulk.
// Applications: measuring "sortedness", rank correlation (Kendall tau), and collaborative filtering.

public class totalInversionsNaive {
    public static void main(String[] args) {
        int arr[] = { 8, 4, 2, 1, 5, 6, 3, 7 };
        System.out.println("Total inversions are: " + inversions(arr, arr.length)); // expected: 13
        // 8 beats [4,2,1,5,6,3,7]=7, 4 beats [2,1,3]=3, 2 beats [1]=1, 5 beats [3]=1, 6 beats [3]=1
        System.out.println(inversions(new int[]{ 1, 2, 3 }, 3)); // expected: 0 (already sorted)
        System.out.println(inversions(new int[]{ 3, 2, 1 }, 3)); // expected: 3 (fully reversed)
    }

    static int inversions(int[] arr, int n) {
        int res = 0;

        for (int i = 0; i < n - 1; i++) {
            // j starts at i+1 so each pair is examined once, with i genuinely before j.
            for (int j = i + 1; j < n; j++) {
                if (arr[i] > arr[j]) {
                    res++;     // a larger value appears BEFORE a smaller one - an inversion
                }
            }
        }
        return res;
    }
}
