// Problem  : Check whether an array is sorted in non-decreasing order.
// Approach : EFFICIENT - compare only ADJACENT pairs; a single out-of-order neighbour disproves it.
// Intuition: Sortedness is TRANSITIVE. If arr[0] <= arr[1] <= arr[2] <= ... holds for every adjacent
//            pair, then every non-adjacent pair is automatically ordered too. So n-1 comparisons
//            carry exactly the same information as the naive version's n(n-1)/2.
// Time     : THETA(n) worst case (n-1 comparisons); O(1) best case (an early break at index 0)
// Space    : THETA(1)
// Trade-off: Optimal - every element must be inspected at least once, since a single unseen element
//            could violate the order. Strictly better than the naive O(n^2) with no downside.

public class isSortedEfficient {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        int arr1[] = { 1, 2, 3, 4, 5, 2 };
        System.out.println(isSorted(arr));                  // expected: true
        System.out.println(isSorted(arr1));                 // expected: false
        System.out.println(isSorted(new int[]{ 2, 2, 2 }));  // expected: true (equal values are allowed)
        System.out.println(isSorted(new int[]{ 7 }));        // expected: true (trivially sorted)
    }

    static boolean isSorted(int[] arr) {
        // Loop to length - 1 because the body reads arr[i + 1]; going to length would run off the end
        // and throw ArrayIndexOutOfBoundsException on the final iteration.
        for (int i = 0; i < arr.length - 1; i++) {

            // Strict '>' means EQUAL neighbours are fine, so this tests NON-DECREASING order.
            // Using '>=' instead would demand strictly increasing values and reject duplicates.
            if (arr[i] > arr[i + 1]) {
                return false;      // one adjacent inversion is enough to disprove sortedness
            }
        }
        return true;
        // Edge: empty and single-element arrays skip the loop (length - 1 is -1 or 0) and correctly
        // return true - both are trivially sorted.
    }
}
