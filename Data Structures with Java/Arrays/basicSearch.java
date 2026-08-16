// Problem  : Find the position of an element x in an array using linear search.
// Approach : Scan left to right, returning the position of the first match.
// Intuition: With no ordering assumption, every element is a possible match, so each must be
//            inspected until one is found. This is the baseline every faster search is measured against.
// Time     : O(n) worst/average, O(1) best (match at the first slot)   Space: O(1)
// Trade-off: Works on ANY array, sorted or not - that generality is its whole value. If the data is
//            sorted, binary search does the same job in O(log n) (see Searching/binarySearch*.java).
//            For repeated lookups on unsorted data, a HashSet/HashMap gives O(1) average instead.
// NOTE      : This duplicates Searching/basicSearch.java; it is kept here as the Arrays-chapter
//            introduction to searching. Compile/run whichever copy you need individually.

class basicSearch {

    public static void main(String[] args) {
        int[] a = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println(linearSearch(a, 5));  // expected: 5  (1-based position)
        System.out.println(linearSearch(a, 99)); // expected: -1 (absent)
    }

    // Returns the 1-BASED position of x, or -1 when x is not present.
    static int linearSearch(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == x) {
                return (i + 1);     // convert the 0-based index into a 1-based position
            }
        }
        return -1;                  // scanned everything without a match
        // Edge cases: an empty array returns -1 (loop never runs); with duplicates the FIRST
        // (leftmost) position is returned, because the scan stops at the earliest match.
    }
}
