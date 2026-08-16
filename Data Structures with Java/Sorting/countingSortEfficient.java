// Problem  : Sort small non-negative integers (0..k-1) STABLY, preserving equal elements' order.
// Approach : EFFICIENT counting sort - tally counts, convert them to a PREFIX SUM (cumulative
//            positions), then place each original element into its exact slot while scanning RIGHT
//            TO LEFT.
// Intuition: The prefix sum turns "how many of each value" into "where does each value's block END".
//            Walking the input BACKWARDS and decrementing that position as we go means the last
//            equal element is placed last - which is precisely what preserves original order.
// Time     : O(n + k)   Space: O(n + k) - a count array plus an output array
// Trade-off: Costs an extra O(n) output array compared with the naive version, and buys STABILITY.
//            That matters enormously: stability is what lets RADIX SORT sort digit by digit and
//            still end up correct (see radixSort.java). It also means real records with satellite
//            data can be sorted, not just bare integers.

public class countingSortEfficient {
    public static void main(String[] args) {
        int[] arr = { 1, 5, 1, 2, 0, 3, 4 };
        int k = 6;                 // values lie in 0..5
        countSort(arr, arr.length, k);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [0, 1, 1, 2, 3, 4, 5]
    }

    static void countSort(int[] arr, int n, int k) {
        int[] count = new int[k];

        // STEP 1 - tally occurrences of each value.
        for (int i = 0; i < n; i++) {
            count[arr[i]]++;
        }

        // STEP 2 - PREFIX SUM. After this, count[v] = how many elements are <= v, which is exactly
        // one past the last index where value v belongs in the sorted output.
        for (int i = 1; i < k; i++) {
            count[i] += count[i - 1];
        }

        int output[] = new int[n];

        // STEP 3 - place elements, scanning the input from RIGHT to LEFT.
        // The direction is what makes this STABLE: the rightmost of several equal elements claims
        // the highest slot in that value's block, so original relative order survives.
        for (int i = n - 1; i >= 0; i--) {
            output[count[arr[i]] - 1] = arr[i];   // -1 converts the count into a 0-based index
            count[arr[i]]--;                       // next equal element goes one slot to the left
        }

        // STEP 4 - copy the sorted output back into the caller's array.
        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
        }
    }
}
