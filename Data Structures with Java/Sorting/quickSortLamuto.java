// Problem  : Sort an array using Quick Sort with LOMUTO's partition scheme.
// Approach : Partition around the LAST element as pivot, then recursively sort the parts strictly
//            left and right of the pivot's now-final position.
// Intuition: Lomuto's partition leaves the pivot at its exact sorted index p. Everything left of p is
//            smaller and everything right is larger, so the pivot never needs to move again - the
//            recursion can safely EXCLUDE it via (l, p-1) and (p+1, h).
// Time     : O(n log n) average; O(n^2) worst case (sorted input, or many duplicates, with a fixed
//            last-element pivot -> every partition peels off just one element)
// Space    : O(log n) average recursion depth; O(n) worst case
// Trade-off: Quicksort is usually the fastest general-purpose sort in practice - in place, excellent
//            cache locality, low constants - which is why Java uses a quicksort variant for
//            PRIMITIVE arrays. Its weaknesses: unstable, and that O(n^2) worst case (mitigated in
//            real implementations by randomized or median-of-three pivot selection).
//            Contrast quickSortHoare.java, whose partition needs a DIFFERENT recursion boundary.

public class quickSortLamuto {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        quickSort(arr, 0, arr.length - 1);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]
    }

    static void quickSort(int[] arr, int l, int h) {
        // BASE CASE: l >= h means 0 or 1 element, which is already sorted.
        if (l < h) {
            int p = lamPart(arr, l, h);      // p is the pivot's FINAL index

            // Safe to exclude p from both calls - Lomuto guarantees it is already in place.
            quickSort(arr, l, p - 1);
            quickSort(arr, p + 1, h);
        }
    }

    static int lamPart(int arr[], int l, int h) {
        int pivot = arr[h];      // pivot = last element
        int i = l - 1;           // boundary of the "< pivot" region (initially empty)

        for (int j = l; j <= h - 1; j++) {
            if (arr[j] < pivot) {
                i++;                          // extend the smaller-region
                int temp = arr[i];            // and swap this element into it
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }

        // Drop the pivot immediately after the smaller region - its final sorted position.
        int temp = arr[i + 1];
        arr[i + 1] = arr[h];
        arr[h] = temp;
        return (i + 1);
    }
    // Why the worst case happens: if the pivot is always the smallest or largest value, one side of
    // the partition is empty and the recursion depth becomes n instead of log n - giving n levels of
    // O(n) work. Randomizing the pivot makes that case vanishingly unlikely.
}
