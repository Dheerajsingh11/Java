// Problem  : Find the maximum sum of any CONTIGUOUS subarray (at least one element).
// Approach : NAIVE - try every (start, end) pair and sum each subarray from scratch.
// Intuition: A subarray is fixed by its two endpoints; brute force just examines all of them and
//            keeps the best sum.
// Time     : O(n^3) - O(n^2) pairs, each summed in O(n)   Space: O(1)
// Trade-off: The most literal solution and the slowest. The Medium version reuses running sums to
//            drop a factor of n; Kadane (Efficient) solves it in a single O(n) pass.

public class kadaneMaxSubarrayNaive {

    static int maxSubarray(int[] a) {
        int n = a.length;
        int best = Integer.MIN_VALUE;           // works even if all elements are negative
        for (int i = 0; i < n; i++) {           // start index
            for (int j = i; j < n; j++) {       // end index (inclusive)
                int sum = 0;
                for (int k = i; k <= j; k++) {  // re-sum a[i..j] every time (the wasteful part)
                    sum += a[k];
                }
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        int[] a = { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        System.out.println(maxSubarray(a)); // 6  -> subarray [4, -1, 2, 1]
        int[] b = { -5, -2, -8 };
        System.out.println(maxSubarray(b)); // -2 -> best single element (all negative)
    }
}
