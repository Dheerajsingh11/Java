// Problem  : Merge two ALREADY-SORTED arrays into one sorted sequence.
// Approach : NAIVE - copy both arrays into one bigger array, then sort the whole thing.
// Intuition: Correct but wasteful: it THROWS AWAY the fact that both inputs are already sorted, and
//            pays a full comparison sort to rediscover an order we were handed for free.
// Time     : O((m+n) log(m+n)) - dominated by Arrays.sort on the combined array
// Space    : THETA(m+n) - the combined array
// Trade-off: Easy to write and hard to get wrong, but asymptotically worse than the two-pointer
//            merge in mergeArrEfficient.java, which runs in THETA(m+n) by exploiting the existing
//            order. Use this only when the inputs are NOT actually sorted.

import java.util.Arrays;

public class mergeArrNaive {
    public static void main(String[] args) {
        int arr1[] = { 1, 2, 3, 4, 5 };
        int arr2[] = { 6, 7 };
        Merge(arr1, arr2, arr1.length, arr2.length);
        System.out.println();   // expected: 1 2 3 4 5 6 7
    }

    static void Merge(int[] a, int[] b, int m, int n) {
        int c[] = new int[m + n];    // must hold every element from both inputs

        // Copy 'a' into the front of c.
        for (int i = 0; i < m; i++) {
            c[i] = a[i];
        }
        // Copy 'b' immediately after, starting at offset m.
        for (int i = 0; i < n; i++) {
            c[m + i] = b[i];
        }

        // The expensive step - and the wasteful one. The concatenated array is "sorted, then sorted"
        // (two ordered runs), so a full O(k log k) sort re-derives information we already had.
        Arrays.sort(c);

        for (int i = 0; i < m + n; i++) {
            System.out.print(c[i] + " ");
        }
    }
    // Side note: Java's Arrays.sort on primitives uses a dual-pivot quicksort. On OBJECTS it uses
    // TimSort, which actually DETECTS pre-sorted runs - so this concatenate-then-sort pattern is
    // less catastrophic there, though the explicit merge is still the right tool.
}
