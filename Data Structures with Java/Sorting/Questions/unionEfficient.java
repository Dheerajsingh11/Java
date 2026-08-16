package Questions;

// Problem  : Print the UNION of two SORTED arrays (every distinct value from either, in sorted order).
// Approach : EFFICIENT - two pointers walking both arrays together, skipping duplicates as they go.
// Intuition: Both inputs are sorted, so the smallest unprinted value is always at one of the two
//            fronts. Comparing just those, and advancing BOTH on a tie, produces the union in order
//            without any sorting or extra storage.
// Time     : O(m + n) - each element is visited once   Space: O(1) (printing directly)
// Trade-off: Beats the naive concatenate-and-sort O((m+n) log(m+n)) by EXPLOITING the existing order.
//            Duplicates are skipped by comparing each element to its predecessor, which is only valid
//            because equal values are adjacent in a sorted array.
// FIX NOTE : The original had two defects. (1) The main loop tested "i < n && j < m" - the bounds
//            were SWAPPED (i indexes a, whose length is m), causing ArrayIndexOutOfBoundsException
//            whenever the arrays differed in length. (2) The drain loops incremented the index only
//            INSIDE the if, so a failed condition looped forever. Both are corrected below.

public class unionEfficient {
    public static void main(String[] args) {
        union(new int[]{ 1, 2, 3, 4, 5 }, new int[]{ 3, 4, 5, 6, 7 }, 5, 5);
        System.out.println();      // expected: 1 2 3 4 5 6 7

        union(new int[]{ 1, 2 }, new int[]{ 5, 5, 5 }, 2, 3);
        System.out.println();      // expected: 1 2 5   (the case that used to crash)
    }

    static void union(int[] a, int[] b, int m, int n) {
        int i = 0, j = 0;

        // Bounds must match the array each index walks: i over a (length m), j over b (length n).
        while (i < m && j < n) {
            // Skip duplicates: in a sorted array, equal values sit next to each other, so an element
            // identical to its predecessor was already printed.
            if (i > 0 && a[i] == a[i - 1]) { i++; continue; }
            if (j > 0 && b[j] == b[j - 1]) { j++; continue; }

            if (a[i] < b[j]) {
                System.out.print(a[i] + " ");
                i++;
            } else if (a[i] > b[j]) {
                System.out.print(b[j] + " ");
                j++;
            } else {
                // Equal - print ONCE and advance both, which is what makes this a union, not a merge.
                System.out.print(a[i] + " ");
                i++;
                j++;
            }
        }

        // Drain the remainder. The index must advance on EVERY iteration, not only when printing -
        // otherwise a duplicate at the drain point spins forever.
        while (i < m) {
            if (i == 0 || a[i] != a[i - 1]) System.out.print(a[i] + " ");
            i++;
        }
        while (j < n) {
            if (j == 0 || b[j] != b[j - 1]) System.out.print(b[j] + " ");
            j++;
        }
    }
}
