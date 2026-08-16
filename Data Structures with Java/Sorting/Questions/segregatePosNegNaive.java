package Questions;

// Problem  : Rearrange an array so all NEGATIVE numbers come before all non-negative ones.
// Approach : NAIVE - two passes into a temporary array (negatives first, then the rest), then copy back.
// Intuition: A two-way partition. Collecting each group in a separate pass is the most direct way to
//            express it, at the cost of scratch space.
// Time     : THETA(n) - three traversals   Space: THETA(n) - the temporary array
// Trade-off: Already linear, but it needs O(n) extra memory. The EFFICIENT version partitions in
//            place with two converging pointers for O(1) space. The one advantage kept here: because
//            each pass preserves original relative order, this version is STABLE - the in-place
//            two-pointer version is NOT.

public class segregatePosNegNaive {
    public static void main(String[] args) {
        int[] arr = { -1, 2, -4, 5, 6, -3, 8, -9, 10 };
        segposneg(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [-1, -4, -3, -9, 2, 5, 6, 8, 10] - negatives first, each group in original order
    }

    static void segposneg(int[] arr, int n) {
        int i = 0;                  // write cursor into temp
        int[] temp = new int[n];

        // PASS 1 - collect the negatives, preserving their relative order.
        for (int j = 0; j < n; j++) {
            if (arr[j] < 0) temp[i++] = arr[j];
        }
        // PASS 2 - append the non-negatives (note >= 0, so zero is grouped with the positives).
        for (int j = 0; j < n; j++) {
            if (arr[j] >= 0) temp[i++] = arr[j];
        }

        // PASS 3 - copy back into the caller's array.
        for (int j = 0; j < n; j++) {
            arr[j] = temp[j];
        }
    }
}
