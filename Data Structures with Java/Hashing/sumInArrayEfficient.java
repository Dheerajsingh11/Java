// Problem  : Does the array contain a SUBARRAY whose elements sum to a given value x?
// Approach : EFFICIENT - running PREFIX SUM plus a HashSet of all prefix sums seen so far.
// Intuition: With P(i) = sum of the first i elements, the subarray (i, j] sums to x exactly when
//            P(j) - P(i) == x, i.e. P(i) == P(j) - x. So at each index we ask the set one question:
//            "have I previously seen a prefix sum equal to (current - x)?" A hit means the elements
//            in between sum to x.
// Time     : O(n) - single pass, O(1) average per set lookup
// Space    : O(n) - one stored prefix sum per index in the worst case
// Trade-off: Converts the naive O(n^2) into O(n) by spending memory. Crucially, this works with
//            NEGATIVE numbers, where the sliding-window technique would fail. Same core idea as
//            zeroSumArrEfficient.java (that is just this problem with x = 0).

import java.util.HashSet;
import java.util.Set;

public class sumInArrayEfficient {
    public static void main(String[] args) {
        int arr[] = { 1, 2, 3, 4, 5, 6 };
        System.out.println(subarrayExists(arr, 12));  // expected: true (3+4+5)
        System.out.println(subarrayExists(arr, 100)); // expected: false
        System.out.println(subarrayExists(new int[]{ 3, 4, -7, 5 }, 0)); // expected: true (negatives OK)
    }

    static boolean subarrayExists(int arr[], int sum) {
        Set<Integer> set = new HashSet<Integer>();
        int curr_sum = 0;

        for (int x : arr) {
            curr_sum += x;                       // extend the prefix

            // Case 1: the prefix ITSELF equals the target (a subarray starting at index 0).
            if (curr_sum == sum) {
                return true;
            }
            // Case 2: some earlier prefix equals curr_sum - sum, so the gap between them sums to
            // exactly 'sum'. This is the rearrangement of P(j) - P(i) == sum.
            if (set.contains(curr_sum - sum)) {
                return true;
            }
            set.add(curr_sum);                   // record this prefix for future queries
        }
        return false;
    }
    // (Seeding the set with 0 instead of the explicit Case 1 check is the equivalent, more compact
    //  formulation - see zeroSumArrEfficient.java, which uses that style.)
}
