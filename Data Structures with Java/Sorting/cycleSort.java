// Problem  : Sort an array while performing the THEORETICAL MINIMUM number of writes.
// Approach : Cycle sort - for each position, count how many elements are smaller to find where the
//            current value belongs, then rotate elements along that cycle into place.
// Intuition: A permutation decomposes into cycles; each element needs to travel around exactly one
//            cycle to reach its final slot. Writing each element only when it moves to its home
//            minimizes total writes - ideal when writes are expensive (e.g. flash memory wear).
// Time     : O(n^2) comparisons   Space: O(1); writes are minimal (each element written at most once)
// Trade-off: Slow in comparisons but unbeatable in write count - a niche win for write-limited media.
//            Also the basis of the "cyclic sort" pattern for arrays holding 1..n.

import java.util.Arrays;

public class cycleSort {

    static void sort(int[] a) {
        int n = a.length;
        for (int start = 0; start < n - 1; start++) {
            int item = a[start];

            // Find where 'item' belongs by counting elements smaller than it (its rank).
            int pos = start;
            for (int i = start + 1; i < n; i++) if (a[i] < item) pos++;

            if (pos == start) continue;         // already in the right place -> no write

            // Skip over duplicates so we do not overwrite an equal element.
            while (item == a[pos]) pos++;
            int t = a[pos]; a[pos] = item; item = t; // place item, pick up the displaced value

            // Rotate the rest of this cycle until we return to 'start'.
            while (pos != start) {
                pos = start;
                for (int i = start + 1; i < n; i++) if (a[i] < item) pos++;
                while (item == a[pos]) pos++;
                t = a[pos]; a[pos] = item; item = t;
            }
        }
    }

    public static void main(String[] args) {
        int[] a = { 5, 2, 4, 1, 3 };
        sort(a);
        System.out.println(Arrays.toString(a)); // [1, 2, 3, 4, 5]
    }
}
