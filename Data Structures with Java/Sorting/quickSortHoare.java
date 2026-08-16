// Problem  : Sort an array using Quick Sort with HOARE's partition scheme.
// Approach : Partition around a pivot with two converging pointers, then recursively sort both sides.
// Intuition: Hoare's partition returns a SPLIT POINT j such that everything in [l..j] is <= pivot and
//            everything in [j+1..h] is >= pivot. Unlike Lomuto, the pivot is NOT guaranteed to be at
//            its final position - so the recursion must INCLUDE index j on the left side.
// Time     : O(n log n) average (balanced splits); O(n^2) worst case (already-sorted input with a
//            fixed first-element pivot produces maximally lopsided partitions)
// Space    : O(log n) average recursion stack; O(n) in the worst case
// Trade-off: Hoare does about 3x fewer swaps than Lomuto and handles duplicates far better, making it
//            the faster scheme in practice. The catch is the subtler recursion boundary below.
// FIX NOTE : The recursion previously read quickSort(arr, l, p - 1), copied from the Lomuto version.
//            That is WRONG for Hoare: since j is a boundary rather than the pivot's resting place,
//            using p-1 skips an element entirely. {1,3,2,8,4,5} came out as {1,2,3,5,4,8}.
//            The correct left call is quickSort(arr, l, p).

public class quickSortHoare {
    public static void main(String[] args) {
        int arr[] = { 1, 3, 2, 8, 4, 5 };
        quickSort(arr, 0, arr.length - 1);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [1, 2, 3, 4, 5, 8]

        int b[] = { 4, 2, 7, 1, 9, 3 };
        quickSort(b, 0, b.length - 1);
        System.out.println(java.util.Arrays.toString(b));   // expected: [1, 2, 3, 4, 7, 9]
    }

    static void quickSort(int[] arr, int l, int h) {
        if (l < h) {
            int p = hoarePart(arr, l, h);

            // NOTE the asymmetry, and why it is required:
            // Hoare returns a BOUNDARY, not the pivot's final index, so the element at p still needs
            // sorting and must stay in the left range. Using (l, p - 1) here would drop it.
            quickSort(arr, l, p);
            quickSort(arr, p + 1, h);
        }
    }

    static int hoarePart(int[] arr, int l, int h) {
        int pivot = arr[l];         // Hoare pivots on the FIRST element
        int i = l - 1, j = h + 1;   // start outside both ends; the do-while loops step inward first

        while (true) {
            do { i++; } while (arr[i] < pivot);   // find something that belongs on the right
            do { j--; } while (arr[j] > pivot);   // find something that belongs on the left

            if (i >= j) {
                return j;                          // pointers crossed - j is the split boundary
            }

            int temp = arr[i];      // both are on the wrong side, so one swap fixes both
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }
}

// ------------------------- TAIL-CALL ELIMINATION -------------------------
// Quicksort's SECOND recursive call is in tail position, so it can be replaced by a loop. Recursing
// only into the SMALLER partition and looping on the larger caps stack depth at O(log n) even in the
// worst case (Java does not optimize tail calls, so this must be done by hand):
//
//   static void quickSort(int[] arr, int l, int h) {
//       while (l < h) {
//           int p = hoarePart(arr, l, h);
//           if (p - l < h - p) { quickSort(arr, l, p); l = p + 1; }   // recurse on the smaller side
//           else               { quickSort(arr, p + 1, h); h = p; }   // loop on the larger side
//       }
//   }
