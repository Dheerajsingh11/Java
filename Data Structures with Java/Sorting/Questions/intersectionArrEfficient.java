package Questions;

// Problem  : Print the INTERSECTION of two SORTED arrays (values in BOTH, without duplicates).
// Approach : EFFICIENT - two pointers advancing through both arrays; on a match print and advance
//            both, otherwise advance whichever pointer holds the smaller value.
// Intuition: If a[i] < b[j], then a[i] cannot appear anywhere later in b (b only grows), so it can be
//            discarded immediately. That single observation means neither pointer ever moves
//            backwards, giving one linear pass instead of a nested search.
// Time     : O(m + n)   Space: O(1)
// Trade-off: Optimal for SORTED inputs and uses no extra memory. For UNSORTED arrays this fails -
//            there you would load one array into a HashSet and probe it, which is O(m+n) time but
//            O(n) space. Sorted data buys the same speed for free.

public class intersectionArrEfficient {
    public static void main(String[] args) {
        int a[] = { 1, 2, 3, 3, 6, 7, 7, 8 };
        int b[] = { 2, 3, 5, 7, 8, 9 };
        intersection(a, b, a.length, b.length);
        System.out.println();      // expected: 2 3 7 8
    }

    static void intersection(int a[], int b[], int m, int n) {
        int i = 0, j = 0;

        while (i < m && j < n) {
            // Skip duplicates in a so each common value is printed only once.
            if (i > 0 && a[i] == a[i - 1]) {
                i++;
                continue;
            }

            if (a[i] < b[j]) {
                i++;          // a[i] is too small to ever match - discard it
            } else if (a[i] > b[j]) {
                j++;          // b[j] is too small to ever match - discard it
            } else {
                System.out.print(a[i] + " ");   // equal -> a common value
                i++;
                j++;                             // advance BOTH past the matched value
            }
        }
        // Once either array is exhausted, no further matches are possible - no drain loop is needed
        // (unlike a merge/union, which must output the leftovers).
    }
}
