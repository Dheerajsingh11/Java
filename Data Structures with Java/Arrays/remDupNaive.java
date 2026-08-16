// Problem  : Remove duplicates from a SORTED array, returning the new logical length.
// Approach : NAIVE - build the de-duplicated sequence in a TEMPORARY array, then copy it back.
// Intuition: Because the array is sorted, all copies of a value are adjacent. So an element is a
//            duplicate exactly when it equals the LAST value written to the output - no searching
//            or set is needed, just one comparison per element.
// Time     : O(n) - one pass to filter plus one pass to copy back
// Space    : O(n) - the temporary array is the weakness this approach carries
// Trade-off: Same linear time as the efficient version, but it allocates a whole second array. Since
//            the output is never longer than the input, that copy is unnecessary - the EFFICIENT
//            version writes the survivors back into the SAME array in place, using O(1) space.

public class remDupNaive {

    public static void main(String[] args) {
        int arr[] = { 1, 1, 2, 3, 4, 4, 5 };
        int n = remDup(arr);
        for (int i = 0; i < n; i++) System.out.print(arr[i] + " ");
        System.out.println();                 // expected: 1 2 3 4 5
        System.out.println("new length = " + n); // expected: 5
    }

    static int remDup(int[] arr) {
        int n = arr.length;
        int[] temp = new int[n];    // O(n) auxiliary storage - the cost of this approach

        temp[0] = arr[0];           // the first element is always kept (nothing precedes it)
        int res = 1;                // count of unique values written so far = next write index

        for (int i = 1; i < n; i++) {
            // temp[res - 1] is the most recently KEPT value. Since the input is sorted, comparing
            // against just that one value is enough to detect a duplicate.
            if (temp[res - 1] != arr[i]) {
                temp[res] = arr[i];
                res++;
            }
        }

        // Copy the unique prefix back so the caller sees the result in the original array.
        for (int i = 0; i < res; i++) {
            arr[i] = temp[i];
        }
        return res;                 // elements beyond this index are stale and simply ignored
    }
    // IMPORTANT: this relies on the array being SORTED. On unsorted input duplicates are not
    // adjacent, so this fails - there you would need a HashSet (O(n) time, O(n) space).
}
