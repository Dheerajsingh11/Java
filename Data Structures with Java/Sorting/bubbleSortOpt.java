// Problem  : Sort an array using Bubble Sort, with the standard optimizations.
// Approach : OPTIMIZED - two improvements over the plain version:
//            (1) shrink the inner loop with "n - i - 1", since the last i elements are already final;
//            (2) exit early via a 'swapped' flag when a pass makes NO swaps.
// Intuition: Each pass "bubbles" the largest remaining element to the end, so that element never
//            needs to be examined again - hence the shrinking range. And if an entire pass performs
//            no swaps, every adjacent pair is already in order, which means the array IS sorted and
//            further passes would be pure waste.
// Time     : O(n) BEST case (already-sorted input exits after one clean pass) - this is the whole
//            point of the flag; THETA(n^2) average and worst case
// Space    : O(1) - in place
// Trade-off: The early exit makes bubble sort genuinely good on nearly-sorted data, but it is still
//            quadratic in general. It is STABLE (equal elements keep their relative order) because
//            only STRICTLY greater elements are swapped.
// FIX NOTE : The original wrote "if (swapped) break;" - inverted logic that aborted after the FIRST
//            productive pass, leaving the array unsorted ({5,4,3,2,1} came out as {4,3,2,1,5}).
//            The correct condition is "if (!swapped) break;" - stop only when NOTHING moved.

public class bubbleSortOpt {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        bubbleSort(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]

        int rev[] = { 5, 4, 3, 2, 1 };   // worst case - the input that exposed the original bug
        bubbleSort(rev, rev.length);
        System.out.println(java.util.Arrays.toString(rev)); // expected: [1, 2, 3, 4, 5]
    }

    static void bubbleSort(int[] arr, int n) {
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;    // did THIS pass move anything?

            // OPTIMIZATION 1: stop at n - i - 1. After i passes, the last i elements are already the
            // i largest values in their final positions, so re-checking them is wasted work.
            for (int j = 0; j < n - i - 1; j++) {

                // Strict '>' keeps the sort STABLE: equal elements are never swapped past each other.
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                    swapped = true;
                }
            }

            // OPTIMIZATION 2: a pass with ZERO swaps proves every adjacent pair is ordered, so the
            // array is fully sorted and we can stop. This is what gives the O(n) best case.
            if (!swapped) {
                break;
            }
        }
    }
}
