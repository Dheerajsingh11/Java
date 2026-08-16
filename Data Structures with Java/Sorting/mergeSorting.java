// Problem  : Sort an array using Merge Sort.
// Approach : DIVIDE AND CONQUER - split the array in half, recursively sort each half, then MERGE
//            the two sorted halves back together with the two-pointer technique.
// Intuition: Merging two sorted lists is cheap (THETA(n), see mergeArrEfficient.java). So instead of
//            sorting directly, we keep halving until each piece has ONE element - which is trivially
//            sorted - and then merge our way back up. All the real work happens in the merges.
// Time     : THETA(n log n) in ALL cases - log n levels of halving, each doing THETA(n) merging.
//            The recurrence is T(n) = 2T(n/2) + THETA(n).
// Space    : THETA(n) - the temporary left/right arrays used during merging (plus O(log n) stack)
// Trade-off: GUARANTEED n log n (unlike quicksort's O(n^2) worst case) and STABLE, which is why
//            Java uses a merge-sort variant (TimSort) for sorting OBJECTS. The price is O(n) extra
//            memory, so quicksort is preferred when memory is tight and in-place sorting matters.
//            Merge sort also shines for EXTERNAL sorting (data larger than RAM) and linked lists.

public class mergeSorting {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        mergeSort(arr, 0, arr.length - 1);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]
    }

    static void mergeSort(int[] arr, int low, int high) {
        // BASE CASE (implicit): when low >= high the range holds 0 or 1 element and is already
        // sorted, so the recursion simply stops.
        if (high > low) {
            // Overflow-safe midpoint: (low + high) could exceed int range on very large arrays.
            int mid = low + (high - low) / 2;

            mergeSort(arr, low, mid);          // DIVIDE + CONQUER: sort the left half
            mergeSort(arr, mid + 1, high);     // sort the right half
            merge(arr, low, mid, high);        // COMBINE: the only step that does real work
        }
    }

    // Merges the two adjacent SORTED runs arr[low..mid] and arr[mid+1..high] back into arr.
    static void merge(int[] arr, int low, int mid, int high) {
        int n1 = mid - low + 1, n2 = high - mid;

        // Copy both halves out, because we are about to overwrite arr[low..high] in place and would
        // otherwise destroy data we still need to read. This is the source of the O(n) space cost.
        int left[] = new int[n1];
        int right[] = new int[n2];
        for (int i = 0; i < n1; i++) left[i] = arr[low + i];
        for (int i = 0; i < n2; i++) right[i] = arr[mid + i + 1];

        int i = 0, j = 0, k = low;   // i -> left, j -> right, k -> write position in arr

        // Take the smaller front element each time. '<=' favours the LEFT half on ties, which is
        // exactly what makes merge sort STABLE.
        while (i < n1 && j < n2) {
            if (left[i] <= right[j]) {
                arr[k++] = left[i++];
            } else {
                arr[k++] = right[j++];
            }
        }

        // Drain whichever half still has elements - it is already sorted, so order is preserved.
        while (i < n1) arr[k++] = left[i++];
        while (j < n2) arr[k++] = right[j++];
    }
}
