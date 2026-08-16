// Problem  : Find an element x in a SORTED array, expressed recursively.
// Approach : EFFICIENT / BINARY SEARCH (recursive) - compare with the middle, then recurse into the
//            single half that can still contain x.
// Intuition: Binary search is naturally recursive: "search this range" reduces to "search a half-sized
//            range". The recursion mirrors the definition, and the base case is an empty range.
// Time     : O(log n) - one comparison per level, log2(n) levels deep
// Space    : O(log n) - each pending call keeps a stack frame (the iterative version is O(1))
// Trade-off: Reads closer to the mathematical definition, but consumes stack proportional to the
//            depth. Java does NOT optimize this tail call away, so for very large inputs prefer
//            binarySearchIterative.java. Note this is still fine here: log2(1e9) is only ~30 frames.

public class binarySearchRecursive {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };

        // NOTE the last index is arr.length - 1, NOT arr.length. Passing arr.length would let 'mid'
        // reach an out-of-bounds index and throw ArrayIndexOutOfBoundsException when x is larger
        // than every element (a bug this file previously had).
        System.out.println(binarySearch(arr, 0, arr.length - 1, 8));  // expected: 7 (0-based index)
        System.out.println(binarySearch(arr, 0, arr.length - 1, 1));  // expected: 0
        System.out.println(binarySearch(arr, 0, arr.length - 1, 11)); // expected: -1 (absent, no crash)
    }

    // Returns the 0-BASED index of x within the inclusive range [low, high], or -1 if absent.
    static int binarySearch(int[] arr, int low, int high, int x) {
        // BASE CASE: the range is empty, meaning every candidate has been ruled out.
        if (low > high) {
            return -1;
        }

        // Overflow-safe midpoint (see binarySearchIterative.java for why (low+high)/2 is unsafe).
        int mid = low + (high - low) / 2;

        if (arr[mid] == x) {
            return mid;                                          // found it
        } else if (arr[mid] > x) {
            return binarySearch(arr, low, mid - 1, x);           // discard mid and everything right
        } else {
            return binarySearch(arr, mid + 1, high, x);          // discard mid and everything left
        }
        // Each recursive call receives a STRICTLY smaller range (mid is excluded), so the recursion
        // is guaranteed to terminate at the low > high base case.
    }
}
