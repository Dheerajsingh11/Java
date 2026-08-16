// Problem  : Sort an array using Bubble Sort, in its plainest form.
// Approach : UNOPTIMIZED - repeatedly sweep the WHOLE array, swapping any adjacent pair that is out
//            of order. Repeat n-1 times.
// Intuition: Each sweep pushes the largest unsorted element to its final place at the right - it
//            "bubbles up" like a bubble rising in water. After n-1 sweeps everything has settled.
// Time     : THETA(n^2) in ALL cases - even a fully sorted array performs every comparison, because
//            nothing here detects that the work is already done
// Space    : O(1) - in place
// Trade-off: This version wastes effort in two ways that bubbleSortOpt.java fixes: it re-scans the
//            already-sorted tail every pass, and it cannot stop early on sorted input. Kept here to
//            make those two optimizations concrete by contrast.

public class bubbleSortUnopt {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        bubbleSort(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]
    }

    static void bubbleSort(int[] arr, int n) {
        // n-1 passes are sufficient: each pass finalizes at least one element, and once n-1 are
        // placed the last one is necessarily correct too.
        for (int i = 0; i < n - 1; i++) {

            // INEFFICIENCY: this always runs to n-1, re-examining the tail that previous passes
            // already sorted. bubbleSortOpt shrinks this bound to n - i - 1.
            for (int j = 0; j < n - 1; j++) {

                // Compare only ADJACENT elements - the defining trait of bubble sort.
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
            // INEFFICIENCY: with no 'swapped' flag, an already-sorted array still burns through all
            // n-1 passes - hence THETA(n^2) even in the best case.
        }
    }
}
