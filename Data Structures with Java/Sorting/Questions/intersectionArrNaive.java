package Questions;

// Problem  : Print the INTERSECTION of two SORTED arrays (values present in BOTH, without duplicates).
// Approach : NAIVE - for each distinct element of a, linear-search the whole of b for a match.
// Intuition: The definition applied literally: "is this value also over there?" Sortedness is used
//            only to skip duplicates in a, not to speed up the search itself.
// Time     : O(m * n) - every element of a may scan all of b   Space: O(1)
// Trade-off: Simple but quadratic. Because BOTH arrays are sorted, a two-pointer merge answers this
//            in O(m + n) (see intersectionArrEfficient.java) - the scan of b is pure waste, since a
//            pointer into b never needs to go backwards.

public class intersectionArrNaive {
    public static void main(String[] args) {
        int a[] = { 1, 2, 3, 3, 6, 7, 7, 8 };
        int b[] = { 2, 3, 5, 7, 8, 9 };
        intersection(a, b, a.length, b.length);
        System.out.println();      // expected: 2 3 7 8
    }

    static void intersection(int[] a, int[] b, int m, int n) {
        for (int i = 0; i < m; i++) {

            // Skip duplicates in a: equal values are adjacent when sorted, so an element identical
            // to its predecessor has already been handled. This keeps the output duplicate-free.
            if (i > 0 && a[i] == a[i - 1]) {
                continue;
            }

            // Search all of b for this value - the O(n) inner cost that makes this quadratic.
            for (int j = 0; j < n; j++) {
                if (a[i] == b[j]) {
                    System.out.print(a[i] + " ");
                    break;                 // one match is enough; stop scanning
                }
            }
        }
    }
}
