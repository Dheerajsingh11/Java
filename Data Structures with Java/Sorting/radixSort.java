// Problem  : Sort non-negative integers without comparing them, even when the value range is large.
// Approach : RADIX SORT - run a STABLE counting sort once per digit position, from the least
//            significant digit (LSD) to the most significant.
// Intuition: Counting sort is only practical when the value range k is small. A digit's range is
//            always tiny (0-9), so we sort by one digit at a time. Because each pass is STABLE, the
//            ordering achieved by earlier (less significant) digits survives later passes - so after
//            processing the most significant digit the array is fully sorted.
// Time     : THETA(d * (n + b)) where d = number of digits and b = base (10 here)
// Space    : THETA(n + b)
// Trade-off: Beats O(n log n) comparison sorts when d is small - ideal for fixed-width keys like
//            IDs, zip codes, or 32-bit integers. It is useless for arbitrary Comparable objects, and
//            it DEPENDS ENTIRELY on the inner sort being stable; swap in the unstable naive counting
//            sort and radix sort silently produces wrong answers.

public class radixSort {
    public static void main(String[] args) {
        int arr[] = { 170, 45, 75, 90, 802, 24, 2, 66 };
        radixsort(arr, arr.length);
        System.out.println(java.util.Arrays.toString(arr));
        // expected: [2, 24, 45, 66, 75, 90, 170, 802]
    }

    static void radixsort(int arr[], int n) {
        // The largest value determines how many digit passes are needed.
        int max = arr[0];
        for (int i = 1; i < n; i++) {
            if (arr[i] > max) max = arr[i];
        }

        // ex is the place value: 1 (units), 10 (tens), 100 (hundreds)...
        // The loop ends once ex exceeds max, i.e. after the most significant digit is processed.
        for (int ex = 1; max / ex > 0; ex = ex * 10) {
            countSort(arr, n, ex);
        }
    }

    // Stable counting sort keyed on the digit selected by place value k.
    static void countSort(int[] arr, int n, int k) {
        int[] count = new int[10];   // exactly 10 possible digits, regardless of how large values get

        // Tally by the current digit: (value / place) % 10 extracts it.
        for (int i = 0; i < n; i++) {
            count[(arr[i] / k) % 10]++;
        }

        // Prefix sum -> cumulative end positions for each digit bucket.
        for (int i = 1; i < 10; i++) {
            count[i] = count[i] + count[i - 1];
        }

        int output[] = new int[n];

        // Right-to-left placement keeps the pass STABLE - the property the whole algorithm rests on.
        for (int i = n - 1; i >= 0; i--) {
            output[count[(arr[i] / k) % 10] - 1] = arr[i];
            count[(arr[i] / k) % 10]--;
        }

        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
        }
    }
    // Limitation: this handles NON-NEGATIVE integers only. Negatives need separate handling (e.g.
    // partition by sign, radix-sort the magnitudes, then reverse the negative block).
}
