// Problem  : Merge two ALREADY-SORTED arrays into one sorted sequence.
// Approach : EFFICIENT - two pointers walking the arrays in parallel, always taking the smaller front
//            element, then draining whatever remains.
// Intuition: Both inputs are sorted, so the next smallest value overall must be at the FRONT of one
//            of them. Comparing just those two candidates is enough - no sorting is required, which
//            is why this beats "concatenate then sort".
// Time     : THETA(m + n) - every element is examined exactly once
// Space    : O(1) here (printing directly); O(m+n) if the result is collected into an array
// Trade-off: Strictly better than the naive concatenate-and-sort O((m+n) log(m+n)) because it
//            EXPLOITS the existing order. This is the "merge" subroutine at the heart of merge sort
//            (mergeSorting.java) and of external sorting for data too large to fit in memory.
// FIX NOTE : The original nested the two drain loops INSIDE the main while loop, so after the first
//            comparison it dumped the rest of both arrays - {1,9} + {2,3} printed "1 9 2 3" instead
//            of "1 2 3 9". It also drained the second array to System.err (the error stream) rather
//            than System.out. Both are corrected below.

public class mergeArrEfficient {
    public static void main(String[] args) {
        Merge(new int[]{ 1, 2, 3, 4, 5 }, new int[]{ 6, 7 }, 5, 2);
        System.out.println();          // expected: 1 2 3 4 5 6 7

        Merge(new int[]{ 1, 9 }, new int[]{ 2, 3 }, 2, 2);
        System.out.println();          // expected: 1 2 3 9  (the case the original got wrong)
    }

    static void Merge(int a[], int b[], int m, int n) {
        int i = 0, j = 0;

        // MAIN MERGE: runs only while BOTH arrays still have elements to compare.
        while (i < m && j < n) {
            // '<=' takes from 'a' on ties, which makes the merge STABLE - equal elements keep the
            // left array's element first. Merge sort's stability depends on exactly this choice.
            if (a[i] <= b[j]) {
                System.out.print(a[i] + " ");
                i++;
            } else {
                System.out.print(b[j] + " ");
                j++;
            }
        }

        // DRAIN: exactly one array still has leftovers, and it is already sorted, so it can be
        // appended wholesale. These loops MUST sit outside the main loop - that was the original bug.
        while (i < m) {
            System.out.print(a[i] + " ");
            i++;
        }
        while (j < n) {
            System.out.print(b[j] + " ");   // System.out, not System.err
            j++;
        }
    }
}
