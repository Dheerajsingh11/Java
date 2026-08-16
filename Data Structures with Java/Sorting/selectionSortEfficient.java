// Problem  : Sort an array using Selection Sort, in place.
// Approach : EFFICIENT - maintain a growing sorted prefix. For each position i, find the minimum of
//            the UNSORTED suffix and swap it into position i.
// Intuition: Once position i holds the smallest of everything from i onward, it is final and never
//            moves again. Swapping (rather than copying to a temp array) means the displaced element
//            simply goes to where the minimum came from - no extra storage and no sentinel needed.
// Time     : THETA(n^2) in ALL cases - the scan for the minimum cannot be skipped, so unlike bubble
//            or insertion sort there is no faster best case
// Space    : O(1) - fully in place
// Trade-off: Selection sort's signature advantage is doing at most n-1 SWAPS - the fewest of any
//            simple sort. That matters when writes are expensive (flash memory, large records).
//            Its drawbacks: always quadratic, and it is NOT STABLE, since a long-distance swap can
//            jump one element past an equal one (e.g. {4a, 4b, 1} -> {1, 4b, 4a}).

public class selectionSortEfficient {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        selectionSort(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]
    }

    static void selectionSort(int[] arr, int n) {
        // Only n-1 iterations are needed: once the first n-1 positions hold the n-1 smallest values,
        // the final element is necessarily the largest and already in place.
        for (int i = 0; i < n - 1; i++) {

            int min = i;    // assume the current position holds the minimum of the remaining suffix

            // Scan only the UNSORTED part [i+1, n). Everything before i is already finalized.
            for (int j = i + 1; j < n; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }

            // One swap puts the minimum in place. The element previously at i is not lost - it
            // simply moves to the slot the minimum vacated, staying inside the unsorted region.
            int temp = arr[min];
            arr[min] = arr[i];
            arr[i] = temp;
        }
    }
    // Comparison count is fixed at n(n-1)/2 regardless of input, which is why there is no O(n)
    // best case here - contrast bubbleSortOpt.java and insertionSort.java, which both have one.
}
