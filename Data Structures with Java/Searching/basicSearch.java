// Problem  : Find the position of an element x in an array (unsorted allowed).
// Approach : NAIVE / LINEAR SEARCH - scan every element from left to right until x is found.
// Intuition: With no ordering to exploit, we have no way to rule out any element without looking at
//            it. So the only correct strategy is to check them all until we hit a match.
// Time     : O(n) worst & average, O(1) best (match at index 0)   Space: O(1)
// Trade-off: The ONLY option when data is unsorted (binary search needs sorted input). If you will
//            search the same array many times, it pays to sort once - O(n log n) - and then use
//            binary search at O(log n) per query instead of O(n).

class basicSearch {

    public static void main(String[] args) {
        int[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println(linearSearch(a, 5));  // expected: 5  (1-based position of value 5)
        System.out.println(linearSearch(a, 99)); // expected: -1 (absent)
    }

    // Returns the 1-BASED position of x, or -1 if x is not present.
    static int linearSearch(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {   // examine each element once
            if (arr[i] == x) {
                return (i + 1);                  // +1 converts the 0-based index to a 1-based position
            }
        }
        // Falling out of the loop means every element was checked and none matched.
        return -1;                               // sentinel for "not found"
    }
    // Edge cases handled:
    //  - empty array   -> loop body never runs -> returns -1
    //  - duplicates    -> returns the FIRST (leftmost) match, because we scan left to right
    //  - x at the end  -> worst case, n comparisons
}
