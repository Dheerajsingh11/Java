// Problem  : Sort an array using Selection Sort.
// Approach : NAIVE - repeatedly find the minimum of the whole array, copy it into a TEMPORARY output
//            array, and neutralize it in the source by overwriting it with Integer.MAX_VALUE.
// Intuition: Selection sort is "repeatedly take the smallest remaining element". This version does
//            that literally, but destroys the input and needs a second array to hold the results.
// Time     : THETA(n^2) - n selections, each scanning n elements
// Space    : THETA(n) - the temporary output array
// Trade-off: Two real weaknesses the EFFICIENT version removes: (1) the O(n) extra array, and (2)
//            the Integer.MAX_VALUE sentinel, which silently BREAKS if the input legitimately
//            contains Integer.MAX_VALUE. The in-place version swaps instead, avoiding both.

public class selectionSortNaive {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        selectionSort(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]
    }

    static void selectionSort(int[] arr, int n) {
        int temp[] = new int[n];        // O(n) auxiliary storage - the cost of this approach

        for (int i = 0; i < n; i++) {
            // Scan the ENTIRE array each time to find the current minimum. Already-consumed slots
            // hold MAX_VALUE, so they lose every comparison and are effectively skipped.
            int min_ind = 0;
            for (int j = 1; j < n; j++) {
                if (arr[j] < arr[min_ind]) {
                    min_ind = j;
                }
            }

            temp[i] = arr[min_ind];              // append the smallest remaining value to the output
            arr[min_ind] = Integer.MAX_VALUE;    // "remove" it by making it unbeatable-large
            // FRAGILITY: if the real data contains Integer.MAX_VALUE, this sentinel is
            // indistinguishable from a genuine value and the sort produces wrong results.
        }

        // Copy the sorted output back so the caller sees it in the original array.
        for (int i = 0; i < n; i++) {
            arr[i] = temp[i];
        }
    }
}
