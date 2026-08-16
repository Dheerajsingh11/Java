package Questions;

// Problem  : Print the UNION of two SORTED arrays (every distinct value from either, in sorted order).
// Approach : NAIVE - concatenate both arrays, sort the result, then print skipping duplicates.
// Intuition: Correct but wasteful - it discards the fact that both inputs are ALREADY sorted and
//            pays for a full comparison sort to rediscover that order.
// Time     : O((m+n) log(m+n)) - dominated by Arrays.sort   Space: O(m+n) for the combined array
// Trade-off: Trivial to write and hard to get wrong, which is its only merit. The two-pointer version
//            (unionEfficient.java) runs in O(m+n) with O(1) extra space by merging the sorted inputs
//            directly. Use this approach only when the inputs are NOT actually sorted.

import java.util.Arrays;

public class unionNaive {
    public static void main(String[] args) {
        int a[] = { 1, 2, 3, 4, 5 };
        int b[] = { 3, 4, 5, 6, 7 };
        union(a, b, a.length, b.length);
        System.out.println();      // expected: 1 2 3 4 5 6 7
    }

    static void union(int a[], int b[], int m, int n) {
        int c[] = new int[m + n];      // must hold every element of both inputs

        for (int i = 0; i < m; i++) c[i] = a[i];
        for (int i = 0; i < n; i++) c[m + i] = b[i];   // b starts at offset m

        Arrays.sort(c);                // the expensive, redundant step

        // Sorting groups duplicates together, so an element differing from its predecessor is the
        // first occurrence of a new value - print only those to get the union.
        for (int i = 0; i < m + n; i++) {
            if (i == 0 || c[i] != c[i - 1]) {
                System.out.print(c[i] + " ");
            }
        }
    }
}
