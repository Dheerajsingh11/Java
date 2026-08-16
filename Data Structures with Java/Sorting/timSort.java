// Problem  : Sort efficiently on REAL data, which is usually partly ordered already.
// Approach : TimSort - a hybrid of insertion sort and merge sort. Split the array into fixed-size
//            "runs", insertion-sort each run, then merge runs pairwise with doubling width.
// Intuition: Two observations drive it. (1) Insertion sort is the fastest algorithm for SMALL arrays
//            because its constants are tiny and it is adaptive - nearly sorted input costs almost
//            nothing. (2) Merge sort is optimal asymptotically but pays overhead on small pieces.
//            TimSort uses each where it wins: insertion sort on small runs, merging above that.
// Time     : O(n log n) worst case; O(n) on already-sorted input   Space: O(n) for merging
// Trade-off: More code than either parent algorithm, in exchange for being fast on real-world data
//            AND stable AND worst-case optimal. This is why it is the default sort for OBJECTS in
//            Java (Arrays.sort / Collections.sort) and in Python.
// NOTE     : This is the simplified textbook version. The real implementation additionally DETECTS
//            natural runs of any length, tracks them on a stack, and merges them under invariants
//            that keep the merges balanced.

import java.util.Arrays;

public class timSort {

    private static final int RUN = 32;      // small-array threshold; real TimSort picks 32-64

    // Standard insertion sort over a slice - used for the small runs.
    static void insertionSort(int[] a, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = a[i], j = i - 1;
            while (j >= left && a[j] > key) {   // '>' keeps it STABLE: equal elements never swap
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }

    // Merge two adjacent sorted slices a[l..m] and a[m+1..r].
    static void merge(int[] a, int l, int m, int r) {
        int n1 = m - l + 1, n2 = r - m;
        int[] left = new int[n1], right = new int[n2];
        for (int i = 0; i < n1; i++) left[i] = a[l + i];
        for (int i = 0; i < n2; i++) right[i] = a[m + 1 + i];

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (left[i] <= right[j]) a[k++] = left[i++];   // '<=' favours the LEFT run -> stable
            else                     a[k++] = right[j++];
        }
        while (i < n1) a[k++] = left[i++];
        while (j < n2) a[k++] = right[j++];
    }

    static void sort(int[] a) {
        int n = a.length;

        // PHASE 1 - insertion-sort each RUN-sized block. Cheap, and on nearly-sorted data the inner
        // while loop barely executes, which is where the adaptивity comes from.
        for (int i = 0; i < n; i += RUN) {
            insertionSort(a, i, Math.min(i + RUN - 1, n - 1));
        }

        // PHASE 2 - merge runs, doubling the width each pass: 32 -> 64 -> 128 ...
        // Starting at RUN (not 1) is the saving: the log2(RUN) cheapest merge levels are skipped
        // entirely, having been handled far more efficiently by insertion sort.
        for (int width = RUN; width < n; width *= 2) {
            for (int left = 0; left < n; left += 2 * width) {
                int mid = Math.min(left + width - 1, n - 1);
                int right = Math.min(left + 2 * width - 1, n - 1);
                if (mid < right) merge(a, left, mid, right);
            }
        }
    }

    public static void main(String[] args) {
        int[] a = { 5, 21, 7, 23, 19, 1, 12, 3, 30, 8, 2, 17 };
        sort(a);
        System.out.println(Arrays.toString(a));
        // expected: [1, 2, 3, 5, 7, 8, 12, 17, 19, 21, 23, 30]

        // Larger input, to exercise the merge phase beyond a single run.
        int[] big = new int[100];
        for (int i = 0; i < big.length; i++) big[i] = (i * 37) % 100;   // scrambled 0..99
        sort(big);
        boolean sorted = true;
        for (int i = 1; i < big.length; i++) if (big[i - 1] > big[i]) sorted = false;
        System.out.println("100 elements sorted correctly: " + sorted);
    }
}

/* ------------------------------ WHY TIMSORT WON ------------------------------
 * Real data is rarely random. Logs arrive nearly in timestamp order; a list re-sorted after a small
 * edit is almost sorted; concatenated datasets are sorted in chunks. TimSort exploits all of that:
 *   - Nearly-sorted input approaches O(n) because insertion sort does almost no work.
 *   - It is STABLE, which is essential for sorting objects by successive keys.
 *   - It is still O(n log n) in the worst case, so adversarial input cannot degrade it.
 *
 * Java uses TimSort for OBJECT arrays and dual-pivot quicksort for PRIMITIVES. The reason for the
 * split is stability: two equal Integers are indistinguishable, so stability is meaningless for
 * primitives and quicksort's better cache behaviour wins. For objects, identity matters and
 * stability is required by the specification.
 * ----------------------------------------------------------------------------- */
