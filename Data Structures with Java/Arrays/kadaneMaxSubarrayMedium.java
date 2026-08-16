// Problem  : Maximum sum of a contiguous subarray.
// Approach : MEDIUM - still try every start, but grow the end while keeping a RUNNING sum, so each
//            subarray extends the previous one in O(1) instead of re-summing.
// Intuition: sum(i, j) = sum(i, j-1) + a[j]. Reusing the previous sum removes the innermost loop of
//            the naive version.
// Time     : O(n^2) - O(n^2) subarrays, each extended in O(1)   Space: O(1)
// Trade-off: A clean factor-of-n win over naive with almost no extra thought. Kadane (Efficient)
//            removes the outer loop too, reaching O(n).

public class kadaneMaxSubarrayMedium {

    static int maxSubarray(int[] a) {
        int n = a.length;
        int best = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int sum = 0;                        // running sum of a[i..j]
            for (int j = i; j < n; j++) {
                sum += a[j];                    // extend the subarray by one element, O(1)
                best = Math.max(best, sum);
            }
        }
        return best;
    }

    public static void main(String[] args) {
        int[] a = { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        System.out.println(maxSubarray(a)); // 6
        int[] b = { -5, -2, -8 };
        System.out.println(maxSubarray(b)); // -2
    }
}
