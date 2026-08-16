package Questions;

// Problem  : Find the k-th SMALLEST element of an array (k is 1-based).
// Approach : NAIVE - sort the whole array, then index directly to position k-1.
// Intuition: Once sorted, the k-th smallest is simply the element at index k-1. Correct and only two
//            lines, but it does far more work than the question asks: it orders ALL n elements when
//            we only care about one.
// Time     : O(n log n) - dominated by the sort   Space: O(1) extra (in-place primitive sort)
// Trade-off: Perfectly reasonable when you need several order statistics or the array is small.
//            Quickselect (kthSmallestElementEfficient.java) exploits the fact that only ONE position
//            matters, achieving O(n) on average by recursing into just one partition. Note this
//            version MUTATES the caller's array.

import java.util.Arrays;

public class kthSmallestElementNaive {
    public static void main(String[] args) {
        int arr[] = { 7, 10, 4, 3, 20, 15 };
        int k = 3;
        System.out.println("Kth smallest element is: " + kthSmallest(arr, arr.length, k));
        // sorted: 3 4 7 10 15 20 -> 3rd smallest = 7
    }

    static int kthSmallest(int arr[], int n, int k) {
        Arrays.sort(arr);      // orders all n elements, though only one index is ever read
        return arr[k - 1];     // k is 1-based, array indices are 0-based
        // Edge: the caller must guarantee 1 <= k <= n, or this throws ArrayIndexOutOfBoundsException.
    }
}
