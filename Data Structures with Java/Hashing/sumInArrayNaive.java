// Problem  : Does the array contain a SUBARRAY whose elements sum to a given value x?
// Approach : NAIVE - try every start index and extend the end, keeping a running sum.
// Intuition: Every subarray is determined by a (start, end) pair, so checking all pairs is guaranteed
//            correct. Carrying a running sum avoids re-adding the whole window each time.
// Time     : O(n^2) - n starts, each extended up to n times, O(1) work per step
// Space    : O(1)
// Trade-off: No extra memory and it works for negative numbers, but quadratic time. The EFFICIENT
//            version uses prefix sums + a HashSet for O(n). (A sliding window would also be O(n) but
//            ONLY for all-positive arrays, since a window cannot shrink correctly with negatives.)

public class sumInArrayNaive {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        System.out.println(subarrayExists(arr, 12)); // expected: true  (3+4+5)
        System.out.println(subarrayExists(arr, 100)); // expected: false
        System.out.println(subarrayExists(arr, 1));  // expected: true  (single element)
    }

    static boolean subarrayExists(int[] arr, int x) {
        for (int i = 0; i < arr.length; i++) {       // every possible START
            int sum = 0;

            for (int j = i; j < arr.length; j++) {   // extend the END
                sum += arr[j];                       // running total of arr[i..j]

                if (sum == x) {
                    return true;                     // found a qualifying subarray
                }
                // We cannot "break early" when sum > x, because negative numbers later in the array
                // could bring the total back down to x. That early exit is only valid for
                // all-positive arrays.
            }
        }
        return false;
    }
}
