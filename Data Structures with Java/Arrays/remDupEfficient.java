// Problem  : Remove duplicates from a SORTED array, returning the new logical length.
// Approach : EFFICIENT / two pointers in place - a READ pointer scans, a WRITE pointer places only
//            the unique values, both moving through the SAME array.
// Intuition: The write pointer can never outrun the read pointer (output is never longer than input),
//            so overwriting is always safe. Sortedness again means duplicates are adjacent, so
//            comparing each element to its immediate predecessor is sufficient.
// Time     : O(n) - a single pass
// Space    : O(1) - no temporary array, unlike the naive version
// Trade-off: Strictly better than the naive approach - same time, no extra memory, and only one pass
//            instead of two. The read/write two-pointer pattern here is the same idea used by
//            zeroesToEndEfficient.java and by partitioning in quicksort.

public class remDupEfficient {

    public static void main(String[] args) {
        int arr[] = { 1, 1, 2, 3, 3, 4, 5, 6, 6, 7 };
        int n = remDups(arr);
        for (int i = 0; i < n; i++) System.out.print(arr[i] + " ");
        System.out.println();                    // expected: 1 2 3 4 5 6 7
        System.out.println("new length = " + n); // expected: 7
    }

    static int remDups(int[] arr) {
        int n = arr.length;
        int res = 1;    // WRITE pointer: next slot for a unique value (index 0 is always kept)

        for (int i = 1; i < n; i++) {   // READ pointer scans the rest
            // Compare with the ORIGINAL previous element. On sorted input, being different from
            // your immediate predecessor is exactly what makes you a new distinct value.
            if (arr[i] != arr[i - 1]) {
                arr[res] = arr[i];      // safe: res <= i always, so we never overwrite unread data
                res++;
            }
        }
        return res;
        // The tail beyond 'res' still holds leftover values, but they lie outside the returned
        // logical length - Java arrays cannot physically shrink, so callers must respect that length.
    }
    // Note: comparing against arr[i-1] (the raw input) rather than arr[res-1] (the last kept value)
    // works only because the array is SORTED; both formulations are equivalent here.
}
