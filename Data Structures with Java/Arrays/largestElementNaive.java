// Problem  : Find the largest element in an array.
// Approach : NAIVE - for each element, check whether ANY other element is greater; the one with no
//            greater rival is the maximum.
// Intuition: This applies the DEFINITION of "largest" literally ("nothing beats it") rather than the
//            smarter running-maximum idea. Correct, but it re-scans the array for every candidate.
// Time     : THETA(n^2) worst case; THETA(n) best case (if the maximum happens to sit at index 0,
//            the very first candidate survives its scan and returns immediately)
// Space    : THETA(1)
// Trade-off: Quadratic for no benefit - the EFFICIENT version answers in a single O(n) pass by
//            carrying the best-so-far. This file exists to make that contrast concrete.

public class largestElementNaive {

    public static void main(String[] args) {
        int arr[] = { 10, 12, 8, 21 };
        System.out.println(largestElement(arr));            // expected: 21
        System.out.println(largestElement(new int[]{ 5 }));  // expected: 5 (single element)
    }

    static int largestElement(int[] arr) {
        int n = arr.length;

        for (int i = 0; i < n; i++) {          // treat each element as a CANDIDATE maximum
            boolean flag = true;                // assume nothing beats it until proven otherwise

            for (int j = 0; j < n; j++) {       // compare the candidate against every element
                if (arr[j] > arr[i]) {
                    flag = false;               // found something bigger -> candidate is disqualified
                    break;                      // no need to keep looking
                }
            }

            if (flag == true) {
                return arr[i];                  // nothing was greater -> this IS the maximum
            }
        }
        return -1;                              // only reachable for an EMPTY array
    }
    // Note: duplicates are handled correctly - the strict '>' means an equal element does not
    // disqualify a candidate, so the first copy of the maximum is returned.
}
