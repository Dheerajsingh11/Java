// Problem  : Sort an array using Heap Sort.
// Approach : Build a MAX-HEAP over the whole array, then repeatedly swap the root (the maximum) with
//            the last unsorted slot and re-heapify the shrinking prefix.
// Intuition: A max-heap always exposes the largest remaining element at index 0. Swapping it to the
//            end places it in its final sorted position, and shrinking the heap by one keeps the
//            sorted suffix growing from the right. The array is thus sorted ascending, in place.
// Time     : O(n log n) in ALL cases - buildHeap is O(n), then n-1 extractions each cost O(log n)
// Space    : O(1) - the heap lives inside the same array (recursion adds O(log n) stack)
// Trade-off: The only common sort with GUARANTEED O(n log n) AND O(1) space - quicksort can degrade
//            to O(n^2), and merge sort needs O(n) extra memory. Its downsides: it is NOT STABLE, and
//            its scattered parent/child jumps are cache-unfriendly, so quicksort usually wins in
//            wall-clock time despite the worse worst case.
// FIX NOTE : The extraction loop previously read "for (i = n-1; i > 1; i--)", stopping at i == 2 and
//            leaving the first TWO elements in heap order rather than sorted order.
//            {12,11,13,5,6,7} came out as {6,5,7,11,12,13}. The bound must be i > 0.

public class heapSort {
    public static void main(String[] args) {
        int[] arr = { 12, 11, 13, 5, 6, 7 };
        HeapSort(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [5, 6, 7, 11, 12, 13]

        int[] two = { 9, 4 };     // the tiny case the old bound got wrong
        HeapSort(two, two.length);
        System.out.println(java.util.Arrays.toString(two)); // expected: [4, 9]
    }

    // Turn an arbitrary array into a max-heap, bottom-up. Starting at the LAST PARENT ((n-2)/2)
    // skips the leaves, which are already valid one-element heaps. This bottom-up build is O(n),
    // not O(n log n), because most nodes sit near the bottom and sift down very little.
    static void buildHeap(int[] arr, int n) {
        for (int i = (n - 2) / 2; i >= 0; i--) {
            maxHeapify(arr, n, i);
        }
    }

    // Restore the max-heap property at index i, assuming both subtrees are already valid heaps.
    // 'n' is the heap's logical size, so the sorted tail beyond n is never touched.
    static void maxHeapify(int[] arr, int n, int i) {
        int largest = i;
        int l = 2 * i + 1;      // left child in the implicit array-based tree
        int r = 2 * i + 2;      // right child

        if (l < n && arr[l] > arr[largest]) largest = l;
        if (r < n && arr[r] > arr[largest]) largest = r;

        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            maxHeapify(arr, n, largest);   // the violation may cascade downward - follow it
        }
    }

    static void HeapSort(int[] arr, int n) {
        buildHeap(arr, n);              // phase 1: O(n)

        // Phase 2: repeatedly move the maximum to the end of the shrinking heap.
        // The bound MUST be i > 0. At i == 1 there are still two unsorted elements, and the final
        // swap is what orders them; stopping at i > 1 leaves them in heap order (descending).
        for (int i = n - 1; i > 0; i--) {
            int temp = arr[0];          // arr[0] is the largest of the remaining heap
            arr[0] = arr[i];
            arr[i] = temp;              // it is now in its FINAL sorted position

            maxHeapify(arr, i, 0);      // heap size shrinks to i, excluding the sorted tail
        }
    }
}
