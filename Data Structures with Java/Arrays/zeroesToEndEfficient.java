// Problem  : Move every 0 in the array to the END, keeping the non-zero elements in order.
// Approach : EFFICIENT / two pointers - 'i' scans the array while 'j' marks the slot where the next
//            non-zero element belongs. Every non-zero found is swapped into position j.
// Intuition: Rather than hunting for a partner to swap with (the naive approach), we maintain the
//            INVARIANT that arr[0..j-1] already holds all non-zeroes seen so far, in order. Any
//            non-zero we encounter simply claims slot j, and whatever was there (necessarily a zero,
//            or itself) is pushed out to i.
// Time     : O(n) - a single pass, one swap at most per element
// Space    : O(1) - fully in place
// Trade-off: Strictly better than the naive O(n^2): same result and same stability, one pass. This
//            is really PARTITIONING (non-zeroes before zeroes), the same mechanic as Lomuto's
//            partition in quicksort and as segregatePosNeg in Sorting/Questions.

public class zeroesToEndEfficient {

    public static void main(String[] args) {
        int arr[] = { 1, 2, 0, 4, 3, 0, 5, 0 };
        zeroesToEnd(arr);
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [1, 2, 4, 3, 5, 0, 0, 0]
    }

    static void zeroesToEnd(int[] arr) {
        int j = 0;   // boundary: everything before j is a non-zero already placed correctly

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != 0) {
                // Swap the non-zero into its rightful slot. When i == j this swaps an element with
                // itself (harmless); otherwise arr[j] is guaranteed to be a ZERO, so the swap
                // simultaneously advances a non-zero forward and pushes a zero backward.
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;

                j++;   // one more non-zero is finalized
            }
            // Zeroes are simply skipped - they get displaced automatically by later swaps.
        }
        // At the end, arr[0..j-1] = all non-zeroes in original order, arr[j..] = all the zeroes.
    }
}
