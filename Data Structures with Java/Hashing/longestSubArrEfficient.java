// Problem  : Find the LENGTH of the longest subarray whose elements sum to a given value.
// Approach : EFFICIENT - running prefix sum plus a HashMap of prefixSum -> FIRST index where it occurred.
// Intuition: The subarray (i, j] sums to 'sum' when P(j) - P(i) == sum, so we look up P(j) - sum.
//            The key insight for MAXIMIZING length: store only the EARLIEST index for each prefix sum.
//            The further left the matching prefix is, the longer the resulting subarray - so
//            overwriting an existing entry would only ever shorten the answer.
// Time     : O(n) - one pass, O(1) average per map operation
// Space    : O(n) - one entry per distinct prefix sum
// Trade-off: O(n^2) -> O(n) at the cost of O(n) memory, and it handles negative numbers (a sliding
//            window cannot). The "keep the first occurrence" rule is what separates this from the
//            existence-only variant in sumInArrayEfficient.java.

import java.util.HashMap;
import java.util.Map;

public class longestSubArrEfficient {
    public static void main(String[] args) {
        int arr[] = { 1, 1, 2, 3, 4, 5, 3, 7 };
        System.out.println(subArray(arr, 10));  // expected: 4  (1+2+3+4)
        System.out.println(subArray(arr, 100)); // expected: 0  (none)
    }

    static int subArray(int[] arr, int sum) {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        int pre_sum = 0;
        int res = 0;

        for (int i = 0; i < arr.length; i++) {
            pre_sum += arr[i];

            // Case 1: the whole prefix arr[0..i] already equals the target. This is the longest
            // possible window ending at i, so it is recorded directly.
            if (pre_sum == sum) {
                res = i + 1;
            }

            // Store only the FIRST time this prefix sum appears. Keeping the earliest index is what
            // maximizes j - i later; overwriting with a newer index would shrink future answers.
            if (map.containsKey(pre_sum) == false) {
                map.put(pre_sum, i);
            }

            // Case 2: an earlier prefix equal to (pre_sum - sum) means the gap after it sums to
            // 'sum'. Its length is the distance between the two indices.
            if (map.containsKey(pre_sum - sum)) {
                res = Math.max(res, i - map.get(pre_sum - sum));
            }
        }
        return res;
    }
}
