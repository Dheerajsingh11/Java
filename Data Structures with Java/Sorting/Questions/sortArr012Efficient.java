package Questions;

// Problem  : Sort an array containing only 0, 1 and 2 - in ONE pass, in place.
// Approach : EFFICIENT - the DUTCH NATIONAL FLAG algorithm. Three pointers carve the array into
//            four regions: [0s | 1s | unexamined | 2s].
// Intuition: Maintain the invariant  arr[0..lo-1] = 0s,  arr[lo..mid-1] = 1s,  arr[hi+1..] = 2s,
//            with arr[mid..hi] still unknown. Each element 'mid' inspects is routed to its region:
//            a 0 is swapped down to lo, a 2 is swapped up to hi, and a 1 is already correct.
// Time     : O(n) - a single pass; each swap permanently places at least one element
// Space    : O(1) - fully in place
// Trade-off: Optimal - one pass and no extra memory, versus the naive version's four passes and O(n)
//            temporary array. The one subtlety (below) is that mid must NOT advance after a 2-swap.
//            The same three-way partition powers 3-way quicksort on duplicate-heavy data.

public class sortArr012Efficient {
    public static void main(String[] args) {
        int arr[] = { 0, 1, 1, 0, 1, 2, 2, 2, 0, 1 };
        sort012(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [0, 0, 0, 1, 1, 1, 1, 2, 2, 2]
    }

    static void sort012(int arr[], int n) {
        int lo = 0, mid = 0, hi = n - 1;

        // Continue while unexamined elements remain. Note '<=': when mid == hi there is still one
        // element left to classify.
        while (mid <= hi) {

            if (arr[mid] == 0) {
                // Send the 0 into the 0-region. Whatever comes back from lo is guaranteed to be a 1
                // (everything in [lo, mid) is 1s), so it is already correct - safe to advance mid.
                int temp = arr[mid]; arr[mid] = arr[lo]; arr[lo] = temp;
                lo++;
                mid++;

            } else if (arr[mid] == 1) {
                mid++;      // already in the right region - just move on

            } else {
                // Send the 2 into the 2-region. The element swapped back from hi has NOT been
                // examined yet, so mid must STAY PUT to classify it on the next iteration.
                // Advancing mid here is the classic bug in this algorithm.
                int temp = arr[mid]; arr[mid] = arr[hi]; arr[hi] = temp;
                hi--;
            }
        }
    }
}
