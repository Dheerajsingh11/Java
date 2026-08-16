// Problem  : Partition an array around a pivot - all elements <= pivot first, then all elements > pivot.
// Approach : NAIVE - two passes into a TEMPORARY array (small elements first, then large), then copy back.
// Intuition: Partitioning is the core operation behind quicksort and quickselect. The simplest correct
//            way is to physically separate the two groups into scratch space and copy them back in order.
// Time     : O(n) - three linear passes
// Space    : O(n) - the temporary array, which is this version's weakness
// Trade-off: Easy to verify but needs O(n) extra memory. Lomuto (lamutoPartition.java) and Hoare
//            (hoarePartition.java) achieve the same partitioning IN PLACE with O(1) space, which is
//            what makes quicksort memory-efficient. Use this file to understand the goal; use the
//            in-place versions in practice.

public class partArray {
    public static void main(String[] args) {
        int arr[] = { 3, 8, 6, 12, 10, 7 };
        partition(arr, 0, arr.length - 1, 5);   // pivot is the element at INDEX 5 (value 7)
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [3, 6, 7, 8, 12, 10] - everything <= 7 first, then everything > 7
    }

    // l, h = inclusive range to partition; p = INDEX of the pivot element.
    static void partition(int[] arr, int l, int h, int p) {
        int temp[] = new int[h - l + 1];
        int index = 0;

        // PASS 1 - collect everything <= pivot, preserving relative order.
        for (int i = l; i <= h; i++) {
            if (arr[i] <= arr[p]) {
                temp[index++] = arr[i];
            }
        }
        // PASS 2 - append everything > pivot after them.
        for (int i = l; i <= h; i++) {
            if (arr[i] > arr[p]) {
                temp[index++] = arr[i];
            }
        }

        // PASS 3 - copy the partitioned result back. temp is 0-based while arr starts at l,
        // hence the (i - l) offset.
        for (int i = l; i <= h; i++) {
            arr[i] = temp[i - l];
        }
    }
    // Note: because the two passes keep original relative order, this partition is STABLE - a
    // property the in-place Lomuto and Hoare schemes give up in exchange for O(1) space.
}
