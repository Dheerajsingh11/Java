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
            do { i++; } while (arr[i] < 0);

            // Advance j left past correctly placed non-negatives, stopping on the first negative.
            do { j--; } while (arr[j] >= 0);

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
    // Safety note: the do-while loops rely on the crossing check to stop them running off the array.
    // An all-negative or all-non-negative input is handled because one pointer reaches the other's
    // sentinel position before any out-of-bounds access occurs.
}
