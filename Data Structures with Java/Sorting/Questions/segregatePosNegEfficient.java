package Questions;

// Problem  : Rearrange an array so all NEGATIVE numbers come before all non-negative ones.
// Approach : EFFICIENT - HOARE-style two-pointer partition. i scans right for a non-negative that is
//            in the wrong place; j scans left for a negative; swap the offending pair.
// Intuition: Each pointer stops only on an element that belongs on the OTHER side, so a single swap
//            corrects two elements at once. When the pointers cross, every element is on its correct
//            side and the partition is complete.
// Time     : THETA(n) - the two pointers together cover the array once   Space: THETA(1) - in place
// Trade-off: Optimal in time and space, versus the naive version's O(n) temporary array. The cost is
//            STABILITY: long-distance swaps scramble the original relative order within each group.
//            Use the naive version when that order must be preserved.
//            This is literally Hoare's partition with "< 0" as the predicate instead of "< pivot".

public class segregatePosNegEfficient {
    public static void main(String[] args) {
        int[] arr = { -1, 2, -4, 5, 6, -3, 8, -9, 10 };
        segposneg(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr));
        // All negatives appear before all non-negatives (exact order within groups is not preserved).
    }

    static void segposneg(int[] arr, int n) {
        int i = -1, j = n;    // start OUTSIDE both ends; the do-while loops step inward first

        while (true) {
            // Advance i past elements already correctly placed (negatives), stopping on the first
            // non-negative - which belongs on the right side.
            // The "i < n - 1" bound is REQUIRED: without it, an array containing no non-negative
            // element lets i run past the last index and throw ArrayIndexOutOfBoundsException.
            do { i++; } while (i < n - 1 && arr[i] < 0);

            // Advance j left past correctly placed non-negatives, stopping on the first negative.
            // The "j > 0" bound guards the mirror case: an array with no negative element.
            do { j--; } while (j > 0 && arr[j] >= 0);

            // Pointers crossed - every element has been assigned to the correct side.
            if (i >= j) {
                return;
            }

            // arr[i] is non-negative but sits on the left; arr[j] is negative but sits on the right.
            // One swap fixes both.
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
    // BOUNDS NOTE: the unguarded form of this algorithm (relying only on the i >= j crossing check)
    // is a genuine bug, not a theoretical one. With an all-negative array the first do-while never
    // finds a non-negative value and walks off the end; with an all-non-negative array the second
    // walks below index 0. Both throw ArrayIndexOutOfBoundsException. The explicit index bounds
    // above are what actually make the scan safe.
}
