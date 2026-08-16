// Problem  : Sort an array by distributing elements into buckets, sorting each bucket, then
//            concatenating them.
// Approach : BUCKET SORT - map each value to one of k buckets by its magnitude, sort each bucket with
//            an ordinary comparison sort, and read the buckets out in order.
// Intuition: If values are spread fairly evenly, each bucket receives only about n/k elements. Sorting
//            many tiny groups is far cheaper than sorting one big array, and because the buckets are
//            ordered by construction, simply concatenating them yields the sorted result.
// Time     : O(n + k) BEST/average case, when elements distribute evenly across buckets;
//            O(n^2) WORST case, when everything lands in one bucket and the inner sort does all the work
// Space    : O(n + k) for the buckets
// Trade-off: Its speed depends entirely on the INPUT DISTRIBUTION - it shines on uniformly spread
//            data (classically, floats in [0,1)) and degrades to the inner sort's complexity when
//            the data is skewed. Unlike counting/radix sort it handles fractional values naturally,
//            but unlike comparison sorts it offers no worst-case guarantee.

import java.util.ArrayList;
import java.util.Collections;

public class bucketSort {
    public static void main(String[] args) {
        int[] arr = { 19, 78, 10, 85, 72, 99, 28 };
        int k = 5;                 // number of buckets
        bucketsort(arr, arr.length, k);
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [10, 19, 28, 72, 78, 85, 99]
    }

    static void bucketsort(int[] arr, int n, int k) {
        // Find the maximum to scale values into bucket indices.
        int max = arr[0];
        for (int i = 1; i < n; i++) {
            max = Math.max(max, arr[i]);
        }
        max++;   // make the range exclusive so the largest element maps to bucket k-1, not k

        // Create k empty buckets.
        ArrayList<ArrayList<Integer>> bucket = new ArrayList<ArrayList<Integer>>();
        for (int i = 0; i < k; i++) {
            bucket.add(new ArrayList<Integer>());
        }

        // SCATTER: map each value proportionally into a bucket. Because the mapping is monotonic
        // (larger value -> same or higher bucket), bucket order equals sorted order.
        for (int i = 0; i < n; i++) {
            int bi = (arr[i] * k) / max;
            bucket.get(bi).add(arr[i]);
        }

        // Sort each bucket individually. Buckets are expected to be small, so the inner sort's
        // quadratic risk rarely bites - unless the distribution is badly skewed.
        for (int i = 0; i < k; i++) {
            Collections.sort(bucket.get(i));
        }

        // GATHER: concatenate the buckets in order, back into the original array.
        int index = 0;
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < bucket.get(i).size(); j++) {
                arr[index++] = bucket.get(i).get(j);
            }
        }
    }
    // Overflow note: (arr[i] * k) can overflow int for very large values; use long, or compute the
    // index as (int)((long) arr[i] * k / max).
}
