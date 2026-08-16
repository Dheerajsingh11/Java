// Problem  : Find the largest element in an array.
// Approach : EFFICIENT - carry a running maximum through a single pass, updating it whenever a bigger
//            element appears.
// Intuition: We never need to compare an element against ALL others - only against the best seen so
//            far. That single remembered value summarizes everything to the left, collapsing the
//            naive nested scan into one sweep.
// Time     : THETA(n) - exactly n-1 comparisons, which is provably OPTIMAL (every element must be
//            examined at least once, or the unseen one could have been the maximum)
// Space    : THETA(1)
// Trade-off: Strictly better than the naive O(n^2) with no downside. This "running best" pattern
//            generalizes widely - it is the same idea behind Kadane's algorithm and min/max tracking.

public class largestElementEfficient {

    public static void main(String[] args) {
        int arr[] = { 10, 12, 8, 21 };
        System.out.println(largestElement(arr));                    // expected: 21
        System.out.println(largestElement(new int[]{ -5, -2, -9 })); // expected: -2 (all negative)
    }

    static int largestElement(int[] arr) {
        // Seed with the FIRST ELEMENT, never with 0. Seeding with 0 would be a classic bug: for an
        // all-negative array it would wrongly report 0, a value not even present in the array.
        int max = arr[0];

        // Start at i = 1 - element 0 is already the incumbent, so comparing it to itself is wasted.
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];      // new champion; everything before i is now summarized by 'max'
            }
        }
        return max;
        // Edge: an EMPTY array throws ArrayIndexOutOfBoundsException on arr[0]. Guard with a
        // length check if empty input is possible - "largest of nothing" has no valid answer.
    }
}
