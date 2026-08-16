// Problem  : Find an element x in a SORTED array much faster than scanning it.
// Approach : EFFICIENT / BINARY SEARCH (iterative) - repeatedly compare with the middle element and
//            discard the half that cannot contain x.
// Intuition: Sorting gives us information for free: if x is smaller than the middle element, x cannot
//            be anywhere to the right. Each comparison therefore eliminates HALF the remaining range.
// Time     : O(log n) - the search space halves each step, so it takes log2(n) steps to reach size 1
// Space    : O(1) - only a few index variables; nothing grows with n (contrast the recursive version)
// Trade-off: Requires SORTED input. The iterative form uses constant space and cannot stack-overflow,
//            which makes it the version to prefer in production over the recursive one.

public class binarySearchIterative {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println(binarySearch(arr, arr.length, 8));  // expected: 8 (1-based position)
        System.out.println(binarySearch(arr, arr.length, 1));  // expected: 1 (first element)
        System.out.println(binarySearch(arr, arr.length, 99)); // expected: -1 (absent)
    }

    // Returns the 1-BASED position of x in the sorted arr, or -1 if absent.
    static int binarySearch(int[] arr, int n, int x) {
        int low = 0, high = n - 1;               // inclusive range [low, high] we still must search

        // Loop while the range is non-empty. Using <= (not <) matters: when low == high there is
        // still exactly ONE unchecked element, and skipping it would miss valid matches.
        while (low <= high) {
            // Why not (low + high) / 2? For very large arrays low+high can EXCEED Integer.MAX_VALUE
            // and overflow to a negative number. This form computes the same midpoint safely.
            int mid = low + (high - low) / 2;

            if (arr[mid] == x) {
                return mid + 1;                  // found; +1 gives a 1-based position
            } else if (arr[mid] > x) {
                high = mid - 1;                  // middle is too big -> x can only be in the LEFT half
            } else {
                low = mid + 1;                   // middle is too small -> x can only be in the RIGHT half
            }
            // Each branch excludes mid itself (mid-1 / mid+1), guaranteeing the range SHRINKS every
            // iteration. Without that, the loop could spin forever on a 2-element range.
        }
        return -1;                               // range became empty -> x is not present
    }
    // Why O(log n): the range length goes n -> n/2 -> n/4 -> ... -> 1, which takes log2(n) halvings.
    // For n = 1,000,000 that is only ~20 comparisons, versus up to 1,000,000 for a linear scan.
}
