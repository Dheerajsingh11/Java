// Problem  : Does the array contain a SUBARRAY whose elements sum to zero? (returns its length, or -1)
// Approach : NAIVE - try every start index and extend the end, maintaining a running sum.
// Intuition: A subarray is fixed by its two endpoints, so brute force simply examines all of them.
//            The running sum makes each extension O(1) instead of re-adding the whole window.
// Time     : O(n^2) - n starts, each extended up to n times
// Space    : O(1) - only a running sum, no auxiliary structures
// Trade-off: No extra memory, but quadratic. The EFFICIENT version uses prefix sums + a HashSet to
//            answer this in O(n) at the cost of O(n) space - see zeroSumArrEfficient.java, which
//            also explains WHY a repeated prefix sum implies a zero-sum subarray.

class zeroSumArrNaive {
    public static void main(String[] args) {
        int arr[] = { 4, 2, -3, 1, 0, 6, 3, -2, 4 };
        System.out.println(subarrayWithZeroSum(arr, arr.length));       // expected: 3  (2, -3, 1)
        System.out.println(subarrayWithZeroSum(new int[]{ 1, -1 }, 2)); // expected: 2
        System.out.println(subarrayWithZeroSum(new int[]{ 1, 2, 3 }, 3)); // expected: -1 (none)
    }

    // Returns the LENGTH of the first zero-sum subarray found, or -1 if none exists.
    static int subarrayWithZeroSum(int arr[], int n) {
        for (int i = 0; i < n; i++) {      // every possible START index
            int sum = 0;                   // reset the running sum for this new start

            for (int j = i; j < n; j++) {  // extend the END one element at a time
                sum += arr[j];             // O(1) update instead of re-summing arr[i..j]

                if (sum == 0) {
                    return j - i + 1;      // inclusive length of the window arr[i..j]
                }
            }
        }
        return -1;                         // no zero-sum subarray exists
    }
}
