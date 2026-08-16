// Problem  : Check whether an array is sorted in non-decreasing order.
// Approach : NAIVE - compare EVERY pair (i, j) with j > i and fail if any later element is smaller.
// Intuition: This checks the definition of sortedness exhaustively: "no element is followed anywhere
//            by a smaller one". Correct, but it verifies far more pairs than necessary.
// Time     : THETA(n^2) worst case (a sorted array must check all n(n-1)/2 pairs before returning true)
// Space    : THETA(1)
// Trade-off: Wasteful, because sortedness is TRANSITIVE - if each element is <= its immediate
//            neighbour, then it is automatically <= everything further right. The EFFICIENT version
//            exploits that to check only n-1 adjacent pairs, giving O(n).

public class isSortedNaive {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        int arr1[] = { 1, 2, 3, 4, 5, 2 };
        System.out.println(isSorted(arr));  // expected: true
        System.out.println(isSorted(arr1)); // expected: false (the trailing 2 breaks the order)
    }

    static boolean isSorted(int[] arr) {
        int n = arr.length;

        for (int i = 0; i < n; i++) {
            // Compare arr[i] against EVERY element to its right - this is the redundant part.
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[i]) {
                    return false;      // found an out-of-order pair - one counterexample is enough
                }
            }
        }
        return true;                   // no violating pair anywhere -> sorted
    }
    // Why this is redundant: to conclude 1 <= 5 we do not need a direct comparison if we already
    // know 1 <= 2, 2 <= 3, 3 <= 4 and 4 <= 5. Transitivity does the rest - which is precisely the
    // shortcut the efficient version takes.
}
