// Problem  : Sort an array of small non-negative integers (values in the range 0..k-1).
// Approach : NAIVE counting sort - tally how many times each value occurs, then rewrite the array by
//            emitting each value that many times, in increasing order.
// Intuition: When the value RANGE is small, we do not need to compare elements at all. Counting
//            occurrences and replaying them in order is enough - which is how this beats the
//            O(n log n) lower bound that applies only to COMPARISON-based sorts.
// Time     : O(n + k) - one pass to count, then k buckets replayed totalling n outputs
// Space    : O(k) - the count array
// Trade-off: Blazing fast when k is small relative to n; useless when k is huge (sorting values up
//            to 1,000,000 needs a million-slot array). The big limitation of THIS version: it
//            REBUILDS values from their counts, so it cannot carry satellite data and is therefore
//            NOT STABLE. countingSortEfficient.java uses prefix sums to place original elements,
//            preserving stability - which is what makes radix sort possible.

public class countingSortNaive {
    public static void main(String[] args) {
        int[] arr = { 1, 4, 1, 2, 0, 3 };
        int k = 5;                 // values are guaranteed to lie in 0..4
        countSort(arr, arr.length, k);
        System.out.println(java.util.Arrays.toString(arr)); // expected: [0, 1, 1, 2, 3, 4]
    }

    static void countSort(int[] arr, int n, int k) {
        // count[v] will hold the number of occurrences of value v.
        // Java zero-fills new int arrays, so the explicit reset loop below is redundant but harmless.
        int[] count = new int[k];
        for (int i = 0; i < k; i++) {
            count[i] = 0;
        }

        // PASS 1 - tally. The VALUE is used directly as an INDEX, which is the whole trick.
        for (int i = 0; i < n; i++) {
            count[arr[i]]++;
        }

        // PASS 2 - replay values in increasing order, each repeated count[i] times.
        int index = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < count[i]; j++) {
                arr[index] = i;      // note: writing the VALUE i, not moving the original element
                index++;
            }
        }
        // Edge: any value >= k (or negative) would throw ArrayIndexOutOfBoundsException here - the
        // caller must guarantee the range. For values in [min..max], offset by min instead.
    }
}
